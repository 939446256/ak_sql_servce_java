package com.example.demo.ak.akSQL.core;

import com.example.demo.ak.akSQL.dto.AKField;
import org.apache.commons.beanutils.BeanMap;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import com.example.demo.ak.akSQL.annotation.*;
import com.example.demo.ak.akSQL.enums.SelectSQLExtendEnum;

import java.lang.reflect.Field;
import java.util.*;

public class SQLSelectTranslate {




    public static String mapToSelectSql(Class c) {
        // 通过注解,获取前缀
        Map<String, String> prefixMap = SQLBaseTranslate.getPrefix(c);

        Map<String, String> customSQLMap = SQLBaseTranslate.getCustomSQLMap(c);

        List<String> paramList = getClassAllKeyToSelectSQL(prefixMap, customSQLMap, c);
        return String.join(",", paramList);
    }



    /**
     * 获取类型中所有Key字段
     *
     * @param prefixMap
     */
    private static List<String> getClassAllKeyToSelectSQL(Map<String, String> prefixMap, Map<String, String> customSQLMap, Class c) {
        Field[] list = c.getDeclaredFields();
        List<String> paramList = new ArrayList();
        for (Field field : list) {
            // 判断是否有忽略注解
            if (field.isAnnotationPresent(SQLIgnore.class)) continue;
            if (field.isAnnotationPresent(SQLEmbedTable.class)) continue;
            if (field.isAnnotationPresent(SQLWhereRange.class)) continue;
            if (field.isAnnotationPresent(SQLWhereTimeRange.class)) continue;
            if (field.isAnnotationPresent(SQLWhereTimePeriod.class)) continue;
            if (field.isAnnotationPresent(SQLSelectExtendVariable.class)) continue;
            if (field.isAnnotationPresent(SQLSelectCollection.class)) continue;

            // 判断是否数组,递归取值
            if (field.getType().getName().equals("java.util.List")) {
                try {
                    Class nextClass = ((Class) ((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0]);
                    List<String> mergeParamList = getClassAllKeyToSelectSQL(prefixMap, customSQLMap, nextClass);
                    paramList.addAll(mergeParamList);
                } catch (Exception err) {
                    System.err.println("自动转换错误提示:数组" + field.getName() + "转换失败");
                }
                continue;
            }

            String fieldName = SQLBaseTranslate.humpToLine(field.getName());

            // 判断是否有自定义(注解)
            String customSQL = customSQLMap.get(field.toString());
            if (StringUtils.hasText(customSQL)) {
                fieldName = customSQL;
            }

            // 判断是否有前缀(注解)
            String prefix = prefixMap.get(field.toString());
            if (StringUtils.hasText(prefix)) {
                if (!fieldName.contains(".")) {
                    fieldName = prefix + "." + fieldName;
                }
            }

            // 判断是否已存在相同字段
            if (paramList.contains(fieldName)) {
                System.err.println("自动转换错误提示," + field.getName() + "存在相同字段!");
                throw new RuntimeException("自动转换错误提示," + field.getName() + " 存在相同字段！");
            }
            // 转小写
            fieldName = fieldName.toLowerCase();
            // 判断是否存在参数扩展(注解)
            fieldName = replaceSQLSelectExtend(c, field, fieldName);
            paramList.add(fieldName);
        }

        return paramList;
    }


    private static String replaceSQLSelectExtend(Class c, Field field, String originFieldName) {
        String fieldName = originFieldName;
        // 扩展注解: 类
        SQLSelectExtend classSqlExtend = (SQLSelectExtend) c.getAnnotation(SQLSelectExtend.class);
        // 扩展注解: 属性
        SQLSelectExtend sqlExtend = field.getAnnotation(SQLSelectExtend.class);
        // 判断是否存在
        if (Objects.isNull(sqlExtend)) {
            if (Objects.isNull(classSqlExtend)) return fieldName;
            sqlExtend = classSqlExtend;
        }
        String tableParamsName = "";
        // 获取命名
        // 判断参数是否存在指定命名
        if (fieldName.contains(" as ")) {
            String[] fildNameSplit = fieldName.split(" as ");
            tableParamsName = fildNameSplit[fildNameSplit.length - 1].replaceAll(" ", "");
            fieldName = fildNameSplit[0];
        }
        // 判断参数是否存在"."分隔
        else if (fieldName.contains(".")) {
            String[] fildNameSplit = fieldName.split("\\.");
            tableParamsName = fildNameSplit[fildNameSplit.length - 1];
        }
        SelectSQLExtendEnum[] selectSQLExtendEnums = sqlExtend.value();
        // 根据扩展枚举处理
        for (SelectSQLExtendEnum enumItem : selectSQLExtendEnums) {
            fieldName = SelectSQLExtendEnum.translateSQL(enumItem, field, fieldName, c);
        }
        if (!StringUtils.isEmpty(tableParamsName)) {
            fieldName = fieldName + " as " + tableParamsName;
        }
        return fieldName;
    }


    /**
     * 变量补充selectSql
     *
     * @param sql       原SQL
     * @param fieldList 需转换属性
     * @param dtoToMap  需转换值
     * @return
     */
    public static String extendSelectSql(String sql, List<Field> fieldList, Map dtoToMap) {
        return SelectSQLExtendEnum.extendSelectSql(sql, fieldList, dtoToMap);
    }
}
