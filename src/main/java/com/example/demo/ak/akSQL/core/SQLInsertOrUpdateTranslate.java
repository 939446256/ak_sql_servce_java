package com.example.demo.ak.akSQL.core;

import org.apache.commons.beanutils.BeanMap;
import com.example.demo.ak.akSQL.annotation.SQLIgnore;
import com.example.demo.ak.akSQL.annotation.SQLSelectCustom;
import com.example.demo.ak.akSQL.annotation.SQLWhereAllowNull;

import java.lang.reflect.Field;
import java.util.*;

public class SQLInsertOrUpdateTranslate {

    /**
     * 生成: 插入/修改SQL
     *
     * @param obj 需要插入/修改的对象
     * @return SQL语句
     */
    public static String mapToSql(Object obj) {
        List<String> allowNullKey = new ArrayList<>();
        // 找出允许为空的字段
        Field[] fieldList = obj.getClass().getDeclaredFields();
        List ignoreList = new ArrayList();
        Map<String, String> customMap = new HashMap<>();
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(SQLWhereAllowNull.class)) allowNullKey.add(field.getName());
            if (field.isAnnotationPresent(SQLIgnore.class)) ignoreList.add(field.getName());
            if (field.isAnnotationPresent(SQLSelectCustom.class))
                customMap.put(field.getName(), field.getAnnotation(SQLSelectCustom.class).value());
        }

        Map dtoToMap = new BeanMap(obj);
        List<String> sqlList = new ArrayList<>();
        for (Object key : dtoToMap.keySet()) {
            if (key.equals("class")) continue;
            if (key.equals("id")) continue;
            if (ignoreList.contains(key)) continue;
            Object item = dtoToMap.get(key);
            // 判断是否为空 和 是否数组类型
            if (!allowNullKey.contains(key)
                    && (Objects.isNull(item) || item instanceof ArrayList)) continue;
            String itemSql = "";
            // 特殊处理, 当带有SQLSelectCustom注解时,取第一个变量
            if (customMap.containsKey(key)) key = customMap.get(key).split(" ")[0];
            // 特殊处理, 当带有SQLWhereAllowNull注解时,可置null
            if (Objects.isNull(item)) {
                itemSql = SQLBaseTranslate.humpToLine(key.toString()) + "= null ";
            } else if (item instanceof Boolean) {
                // 布尔类型
                itemSql = SQLBaseTranslate.humpToLine(key.toString()) + "=" + item.toString();
            } else {
                // 字符串类型
                itemSql = SQLBaseTranslate.humpToLine(key.toString()) + "=\"" + item.toString().replace("\"", "\\\"") + "\"";
            }
            sqlList.add(itemSql);
        }
        if (sqlList.size() == 0) {
            throw new RuntimeException("参数不能为空");
        }
        return String.join(",", sqlList);
    }

}
