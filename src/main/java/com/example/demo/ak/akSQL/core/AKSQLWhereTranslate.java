package com.example.demo.ak.akSQL.core;

import com.example.demo.ak.akSQL.annotation.*;
import com.example.demo.ak.akSQL.dto.AKClass;
import com.example.demo.ak.akSQL.dto.AKField;
import com.example.demo.ak.akSQL.dto.AKObject;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

public class AKSQLWhereTranslate {

    /**
     * 时间范围字段后缀
     */
    private static final String SQL_WHERE_TIME_RANGE_START_SUFFIX = "Start";
    private static final String SQL_WHERE_TIME_RANGE_END_SUFFIX = "End";

    /**
     * 生成: 查询SQL
     *
     * @param obj 转换对象属性
     * @return SQL语句
     */
    public static String mapToQuerySql(AKObject obj, AKClass c) {
        return mapToQuerySql(obj, c, true);
    }

    /**
     * 生成: 查询SQL
     *
     * @param obj 转换对象属性
     * @return SQL语句
     */
    public static String mapToQuerySql(AKObject obj, AKClass c, Boolean hasPrefix) {
        // 通过注解,获取前缀
        Map<String, String> prefixMap = hasPrefix ? AKSQLBaseTranslate.getPrefix(c) : new HashMap<>();
        Map<String, String> customSQLMap = AKSQLBaseTranslate.getCustomSQLMap(c);
        List<AKField> fieldList = c.getDeclaredFields();
        // 如果为空, 则构造空对象
        if (Objects.isNull(obj)) {
            try {
//                obj = c.getConstructor().newInstance();
                obj = c.getConstructor();
            } catch (Exception err) {
            }
        }
//        Map dtoToMap = new BeanMap(obj);
        Map dtoToMap = obj.getMap();
        if(Objects.isNull(dtoToMap)) dtoToMap = new HashMap<>();
        List<String> sqlList = new ArrayList<>();

        // 特殊处理时间范围(缓存参数)
        Map<String, String> timeRange = new HashMap<>();
        // 特殊处理 条件"or" (缓存参数)
        Map<String, List<String>> orSQLMap = new HashMap<>();
        // 特殊处理 记录自定义格式替换参数
        List<Map> customSQLList = new ArrayList<>();
        String sqlWhereCustomString = null;
        if(c.isAnnotationPresent(SQLWhereCustom.class)){
            sqlWhereCustomString = c.getAnnotation(SQLWhereCustom.class).toString();
        }


        // 遍历对象所有字段
        for (AKField field : fieldList) {
            String baseKey = AKSQLBaseTranslate.underlineToCamel(field.getName());
            String resultKey = AKSQLBaseTranslate.underlineToCamel(field.getName());
            String allKey = field.toString();
            // 跳过特殊字符
            if (field.getName().equals("serialVersionUID")) { continue; }



            String itemSql = "";
            String custom = customSQLMap.get(allKey);
            String keyToLine = SQLBaseTranslate.humpToLine(StringUtils.hasText(custom) ? custom : baseKey.toString());

            // 判断是否添加前缀
            String prefix = prefixMap.get(allKey);
            if (StringUtils.hasText(prefixMap.get(allKey))) {
                keyToLine = prefix + "." + keyToLine;
                if (keyToLine.contains(" as ")) {
                    String[] prefixSplitList = keyToLine.split(" as ");
                    keyToLine = prefixSplitList[0];
//                    key = prefixSplitList[1];
                    resultKey = AKSQLBaseTranslate.underlineToCamel(prefixSplitList[1]);
                }
            }


            Object item = dtoToMap.get(resultKey);
            // 默认过滤删除
            if (field.getName().equals("deleted") && Objects.isNull(item)) item = Boolean.FALSE;
            // 判断是否为空 和 是否数组类型
            if (!field.isAnnotationPresent(SQLWhereAllowNull.class) && (Objects.isNull(item) || item instanceof ArrayList)) continue;
            // 判断是否忽略
            if (field.isAnnotationPresent(SQLIgnore.class)) continue;
            // 判断是否select条件
            if (field.isAnnotationPresent(SQLSelectExtendVariable.class)) continue;
            if (field.isAnnotationPresent(SQLTableWhere.class)) continue;


            // 特殊处理时间范围
            // 命名例子: 参数名称+Start 和 参数名称+End
//            if (field.isAnnotationPresent(SQLWhereTimeRange.class)) {
//                // 时间范围
//                if (resultKey.contains(SQL_WHERE_TIME_RANGE_START_SUFFIX)) {
//                    resultKey = resultKey.substring(0, baseKey.lastIndexOf(SQL_WHERE_TIME_RANGE_START_SUFFIX));
//                } else if (resultKey.contains(SQL_WHERE_TIME_RANGE_END_SUFFIX)) {
//                    resultKey = resultKey.substring(0, baseKey.lastIndexOf(SQL_WHERE_TIME_RANGE_END_SUFFIX));
//                }
//            }


            // 特殊处理createdTimeStart和createdTimeEnd时间范围
            if (field.isAnnotationPresent(SQLWhereTimeRange.class) || field.isAnnotationPresent(SQLWhereRange.class)) {
                // 时间范围
                AKField tempField = new AKField();
                tempField.setName(resultKey);
                List<String> range = filteTimeRange(item, tempField, timeRange);
                if (Objects.nonNull(range) && range.size() == 2) {
                    // 保证是下划线
                    String humpToLineKey = AKSQLBaseTranslate.humpToLine(keyToLine);
                    if(field.isAnnotationPresent(SQLWhereRange.class)) {
                        itemSql = " " + humpToLineKey + " BETWEEN " + range.get(0) + " AND " + range.get(1);
                    } else {
                        itemSql = " (UNIX_TIMESTAMP(" + humpToLineKey + ")*1000 BETWEEN " + range.get(0) + " AND " + range.get(1) + ") ";
                    }
//                    itemSql = keyToLine + " >= \"" + range.get(0) + "\" AND " + keyToLine + " <= \"" + range.get(1) + "\"";
                } else {
                    continue;
                }
            }
            else if (item instanceof Boolean) {
                // 布尔类型
                itemSql = keyToLine + "=" + item.toString();
            } else {
                // 字符串类型
                // 判断条件
                if (field.isAnnotationPresent(SQLWhereEqual.class)) {
                    if(Objects.isNull(item)){
                        itemSql = keyToLine + " = " + item;
                    } else {
                        itemSql = keyToLine + " = \"" + item.toString() + "\"";
                    }
                } else if (field.isAnnotationPresent(SQLWhereNotEqual.class)) {
                    if(Objects.isNull(item)){
                        itemSql = keyToLine + " != " + item;
                    } else {
                        itemSql = keyToLine + " != \"" + item.toString() + "\"";
                    }
                } else if (field.isAnnotationPresent(SQLWhereSymbol.class)) {
                    String symbol = field.getAnnotation(SQLWhereSymbol.class).toString();
                    itemSql = keyToLine + " " + symbol + " \"" + item.toString() + "\"";
                } else if (field.isAnnotationPresent(SQLWhereIn.class)) {
                    String[] items = item.toString().split(",");
                    List newItems = new ArrayList();
                    for (String itemStr : items) {
                        newItems.add("\"" + itemStr + "\"");
                    }
                    itemSql = keyToLine + " in (" + String.join(",", newItems) + ")";
                }
                else if (Objects.isNull(item)) {
                    itemSql = keyToLine + " is NULL ";

                }
                else {
                    itemSql = keyToLine + " LIKE concat('%', \"" + item.toString() + "\",'%')";
                }
            }


            // 自定义查询条件
            if (field.isAnnotationPresent(SQLWhereCustomParams.class)) {
                String customKey = field.getAnnotation(SQLWhereCustomParams.class).toString();
                if(StringUtils.isEmpty(customKey)){
                    customKey = "";
                }
                Map<String,String> customSQLs = new HashMap<>();
                customSQLs.put("name","#{" + customKey + "}");
                customSQLs.put("value", itemSql);
                customSQLList.add(customSQLs);
            }
            // 特殊处理 "or"
            else if (field.isAnnotationPresent(SQLWhereOrGroup.class)) {
                String groupId = field.getAnnotation(SQLWhereOrGroup.class).toString();
                List<String> orSQLs = orSQLMap.get(groupId);

                if (Objects.isNull(orSQLs)) {
                    orSQLs = new ArrayList<>();
                    orSQLs.add(itemSql);
                    orSQLMap.put(groupId, orSQLs);
                } else {
                    orSQLs.add(itemSql);
                }
            } else {
                sqlList.add(itemSql);
            }
        }

        // 特殊处理 "or"
        for (String key : orSQLMap.keySet()) {
            List<String> orSQLs = orSQLMap.get(key);
            sqlList.add("(" + String.join(" OR ", orSQLs) + ")");
        }

        if(customSQLList.size() > 0){
            for (Map<String,String> map : customSQLList) {
                String key = map.get("name");
                String value = map.get("value");
                sqlWhereCustomString = sqlWhereCustomString.replace(key,value);
            }
            sqlList.add(sqlWhereCustomString);
        }

        return String.join(" AND ", sqlList);
    }


    private static List<String> filteTimeRange(Object item, AKField field, Map<String, String> timeRange) {
        String key = field.getName();
        timeRange.put(key, item.toString());
        // 判断是否开始时间参数
        if (key.contains(SQL_WHERE_TIME_RANGE_START_SUFFIX)) {
            String prefixFieldName = key.substring(0, key.lastIndexOf(SQL_WHERE_TIME_RANGE_START_SUFFIX));
            String endKey = prefixFieldName + SQL_WHERE_TIME_RANGE_END_SUFFIX;
            String endTime = timeRange.get(endKey);
            if (Objects.nonNull(endTime)) {
                return Arrays.asList(item.toString(), endTime);
            }
        }
        // 判断是否结束时间参数
        else if (key.contains(SQL_WHERE_TIME_RANGE_END_SUFFIX)) {
            String prefixFieldName = key.substring(0, key.lastIndexOf(SQL_WHERE_TIME_RANGE_END_SUFFIX));
            String startKey = prefixFieldName + SQL_WHERE_TIME_RANGE_START_SUFFIX;
            String startTime = timeRange.get(startKey);
            if (Objects.nonNull(startTime)) {
                return Arrays.asList(startTime, item.toString());
            }
        }

        return null;
    }



}
