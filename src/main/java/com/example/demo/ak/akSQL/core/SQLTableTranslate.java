package com.example.demo.ak.akSQL.core;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;
import com.example.demo.ak.akSQL.annotation.SQLAssociativeTable;
import com.example.demo.ak.akSQL.annotation.SQLTableWhere;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import static top.tangyh.basic.context.ContextConstants.TENANT_BASE_POOL_NAME_HEADER;

public class SQLTableTranslate {

    private static Map<String, Map<String, String>> relationMap;

    /**
     * 生成: 联表SQL
     * 注意: 新表需要添加到 relationList 数组中
     *
     * @param c 含有SQLAssociativeTable注解的类
     * @return SQL语句
     */
    public static String listToTableSql(Class c) {
        // 不指定tenant, 则取Context的tenantId
        return listToTableSql(c,null);
    }

    /**
     * 生成: 联表SQL
     * 注意: 新表需要添加到 relationList 数组中
     *
     * @param c 含有SQLAssociativeTable注解的类
     * @param tenantId 指定tenant, 不指定则取Context的tenantId
     * @return SQL语句
     */
    public static String listToTableSql(Class c, String tenantId) {
        SQLAssociativeTable associativeTable = (SQLAssociativeTable) c.getAnnotation(SQLAssociativeTable.class);
        Map tableWhereMap = new HashMap();
        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(SQLTableWhere.class)) {
                String sqlTableWhere = field.getAnnotation(SQLTableWhere.class).value();
                String key = sqlTableWhere.split("\\.")[0];
                tableWhereMap.put(key, sqlTableWhere);
            }
            ;
        }
        if (Objects.isNull(associativeTable)) {
            throw new RuntimeException("解析对象" + c.getName() + "缺少注解 SQLAssociativeTable");
        }

        // []转List
        String[] associativeTableList = associativeTable.value();
        ArrayList<String> arrayList = new ArrayList<>(associativeTableList.length);

        // 遍历填充租户数据源
        for (String table : associativeTableList) {
            String newTable = addTenantId(table, tenantId);
            arrayList.add(newTable);
        }
        return listToTableSql(arrayList, tableWhereMap);
    }

    /**
     * 联表: 关联列表中所有数据表
     *
     * @param tableArray 需要关联数据表
     * @return
     */
    static String listToTableSql(List<String> tableArray, Map tableWhereMap) {
        // 分离别称
        Map<String, String> prefixMap = new HashMap<>();
        List<String> orginTableMap = JSONObject.parseObject(JSONObject.toJSONString(tableArray), List.class);
        for (String item : tableArray) {
            String[] items = item.split(" ");
            if (items.length > 1) {
                // 分离跨库
                String[] acrossT = items[0].split("\\.");
                if (acrossT.length > 1) {
                    prefixMap.put(acrossT[1], items[1]);
                } else {
                    prefixMap.put(items[0], items[1]);
                }
                Collections.replaceAll(tableArray, item, items[0]);
            }
        }
        // 分离跨库
        Map<String, String> acrossTableMap = new HashMap<>();
        for (String item : tableArray) {
            String[] items = item.split("\\.");
            if (items.length > 1) {
                acrossTableMap.put(items[1], items[0]);
                Collections.replaceAll(tableArray, item, items[1]);
            }
        }

        // 创建连表单
        if(Objects.isNull(relationMap)) createRelationMap();

        String joinTable = "";
        String sql = "";
        Integer index = 0;
        // 遍历所有数据表
        for (String item : tableArray) {
            String orginTable= orginTableMap.get(index);
            index++;

            // 使用正则找到自定义内容
            Pattern p = Pattern.compile("on\\s*\\((.*)\\)", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(orginTable);
            if(m.find()) {
                String[] orginTableSplit = orginTable.split(" ");
                // 拼接SQL语句
                sql += " LEFT JOIN " + orginTable ;
                sql += " AND " + orginTableSplit[1] + ".deleted = false ";
            } else {
                String currentPrefix = prefixMap.get(item);
                String acrossTable = acrossTableMap.get(item);
                acrossTable = StringUtils.isEmpty(acrossTable) ? "" : acrossTable + ".";
                // 判断是否遍历第一次
                if (StringUtils.isEmpty(sql)) {
                    if (StringUtils.isEmpty(currentPrefix)) {
                        sql += item;
                    } else {
                        sql += item + " " + currentPrefix;
                    }
                    if (!StringUtils.isEmpty(acrossTable)) {
                        sql = acrossTable + sql;
                    }
                    continue;
                }

                // 遍历找出对应数据表
                String[] relations = new String[0];
                for (String table : tableArray) {
                    String relation;
                    try {
                        relation = relationMap.get(table).get(item);
                    } catch (Exception err) {
                        throw new RuntimeException(item + "表,没有找到关联表");
                    }
                    if (Objects.nonNull(relation)) {
                        joinTable = table;
                        relations = relation.split(",");
                        break;
                    }
                }
                // 判断关联参数非空
                if (relations.length == 0) {
                    throw new RuntimeException(item + "表,没有找到关联表");
                }

                // 判断是否存在别称
                // 获取关联表别称
                String joinTablePrefix = prefixMap.get(joinTable);
                if (!StringUtils.isEmpty(joinTablePrefix)) joinTable = joinTablePrefix;
                // 基础表
                String baseTableName = acrossTable + item;
                // 判断基础表存在别称
                String joinBaseTableName = StringUtils.isEmpty(currentPrefix) ? baseTableName : currentPrefix;
                if (Objects.isNull(currentPrefix)) currentPrefix = "";
                // 拼接SQL语句
                sql += " LEFT JOIN " + baseTableName + " " + currentPrefix + " on " +
                        joinTable + "." + relations[0] + "=" +
                        joinBaseTableName + "." + relations[1];
                // 判断是否存在SQLTableWhere添加字段
                if (tableWhereMap.size() > 0
                        && tableWhereMap.containsKey(currentPrefix)) {
                    sql += " AND " + tableWhereMap.get(currentPrefix);
                }
                // 拼接排除删除
                if(baseTableName.contains("def_user") == false
                        && baseTableName.contains("base_role") == false
                        && baseTableName.contains("def_tenant") == false
                        && baseTableName.contains("def_resource") == false
                        && baseTableName.contains("def_resource_api") == false){
                    sql += " AND " + joinBaseTableName + ".deleted = false ";
                }
            }

        }
        return sql;
    }



    // ===========================  封装方法 ===============================
    private static void createRelationMap(){
        relationMap = new HashMap();
        // 处理关系表联表
        for (List<String> tableList : SQLAssociativeTable.relationList) {
            String table1 = tableList.get(0);
            String key1 = tableList.get(1);
            String table2 = tableList.get(2);
            String key2 = tableList.get(3);

            // 判断table1是否存在
            Map<String, String> relationTable1 = relationMap.get(table1);
            if (Objects.isNull(relationTable1)) {
                relationTable1 = new HashMap<>();
            } else {
                if (!Objects.isNull(relationTable1.get(table2))) continue;
            }

            relationTable1.put(table2, key1 + "," + key2);
            relationMap.put(table1, relationTable1);

            // 判断table2是否存在
            Map<String, String> relationTable2 = relationMap.get(table2);
            if (Objects.isNull(relationTable2)) {
                relationTable2 = new HashMap<>();
            } else {
                if (!Objects.isNull(relationTable2.get(table2))) continue;
            }

            relationTable2.put(table1, key2 + "," + key1);
            relationMap.put(table2, relationTable2);
        }

    }


    // 转换对应租户
    private static String addTenantId(String table , String tenantId) {
//        String[] tables = table.split("\\.");
//        // 判断没有指定数据源
//        if (tables.length == 1) {
//            // 指定该租户数据源
//            tenantId = Objects.isNull(tenantId) ? ContextUtil.get(TENANT_BASE_POOL_NAME_HEADER) : tenantId;
//            if (StringUtils.hasText(tenantId)) {
//                table = TENANT_BASE_POOL_NAME_HEADER + "_" + tenantId + "." + table;
//            } else {
//                table = "lamp_ds_c_defaults." + table;
//            }
//        }
//        return table;
        return table;
    }
}
