package com.example.demo.ak.akSQL.enums;


import cn.hutool.core.util.StrUtil;
import com.example.demo.ak.akSQL.annotation.SQLWhereRange;
import com.example.demo.ak.akSQL.dto.AKClass;
import com.example.demo.ak.akSQL.dto.AKField;
import lombok.Getter;
import com.example.demo.ak.akSQL.annotation.SQLSelectExtendVariable;
import com.example.demo.ak.akSQL.annotation.SQLTableNick;
import com.example.demo.ak.akSQL.annotation.SQLWhereTimeRange;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public enum SelectSQLExtendEnum {

    // 自定义 查询参数扩展
    // 特殊字符替换:{tableNick} 表别名
    // 特殊字符替换:{resource} 数据源

    // 判空赋值
    DEFAULT_NUMBER_VALUE("IFNULL(%s,0)", "判空赋值"),
    // 统计数量
    COUNT("COUNT(%s)", "统计数量"),
    // 合计
    SUM("SUM(%s)", "合计"),
    // 判空
    IS_NOT_NULL("!ISNULL(%s)", "判空"),
    // 转换为是否
    TRUE_OR_FALSE("IF(%s,\"是\",\"否\")", "转换为是否"),
    // 分组
    GROUP_CONCAT("GROUP_CONCAT(%s ORDER BY {tableNick}.created_time DESC )", "分组"),
    // 分组: 获取排序第一个
    LAST_ONE_GROUP_CONCAT("SUBSTRING_INDEX( GROUP_CONCAT(%s ORDER BY {tableNick}.created_time DESC ), ',', 1 )", "分组: 获取排序第一个"),
    // 查询租户公司名称
    GET_TENANT_NAME("(SELECT name FROM lamp_ds_c_defaults.def_tenant tenant WHERE tenant.id = %s LIMIT 1)"),
    // 查询用户账号
    GET_USER_NAME("(SELECT username FROM lamp_ds_c_defaults.def_user d_user WHERE d_user.id = %s LIMIT 1)"),
    // 查询用户昵称
    GET_NICK_NAME("(SELECT nick_name FROM lamp_ds_c_defaults.def_user d_user WHERE d_user.id = %s LIMIT 1)"),
    // 查询员工名称
    GET_EMPLOYEE_NAME("(SELECT real_name FROM {resource}.base_employee e WHERE e.id = %s LIMIT 1)", "查询员工名称"),
    // 查询员工名称
    GET_EMPLOYEE_NAME_BY_USER_ID("(SELECT real_name FROM {resource}.base_employee e WHERE e.user_id = %s LIMIT 1)", "查询员工名称"),
    // 查询设备原始产量
    GET_DEVICE_ORIGINAL_PRODUCT("(SELECT dod.product FROM lamp_ds_c_collector.device_original_data dod WHERE dod.ip = {tableNick}.ip and dod.mac = {tableNick}.mac LIMIT 1)"),
    // 查询设备实时产量
    GET_DEVICE_REAL_TIME_PRODUCT("(SELECT drd.product FROM lamp_ds_c_collector.device_realtime_data drd WHERE drd.ip = {tableNick}.ip and drd.mac = {tableNick}.mac LIMIT 1)"),
    // 获取订单统计入库库存
    GET_PUT_STORAGE_ORDER_COUNT("(SELECT SUM( number ) FROM {resource}.c_storage_order_goods sog LEFT JOIN {resource}.c_storage_order so ON sog.storage_order_id = so.id WHERE sog.goods_id = g.id and so.status = \"FINISH\" and so.type = 1 {warehouseId} {so.date})"),
    // 获取订单统计出库库存
    GET_OUT_STORAGE_ORDER_COUNT("(SELECT SUM( number ) FROM {resource}.c_storage_order_goods sog LEFT JOIN {resource}.c_storage_order so ON sog.storage_order_id = so.id WHERE sog.goods_id = g.id and so.status = \"FINISH\" and so.type = 2 {warehouseId} {so.date})"),
    // 查询班次名称
    GET_CLASSES_NAME("(SELECT name FROM {resource}.c_scheduling s WHERE s.id = %s LIMIT 1)"),

    //连表-获取订单统计入库库存
    GET_ASS_PUT_STORAGE_ORDER_COUNT("SUM(CASE WHEN so.type = 1 THEN sog.number END)"),
    //连表-获取订单统计出库库存
    GET_ASS_OUT_STORAGE_ORDER_COUNT("SUM(CASE WHEN so.type = 2 THEN sog.number END)"),

    // 获取商品现库存数量
    GET_STORAGE_COUNT("(SELECT SUM(count) FROM {resource}.c_storage_goods sg where sg.goods_id = g.id and sg.warehouse_id = 1   group by sg.goods_id )"),
    // 获取商品分类名称
    GET_GOODS_CLASSES_NAME("(SELECT name FROM {resource}.c_goods_classes gc WHERE gc.id = %s LIMIT 1)"),
    // 获取往来单位名称
    GET_UNIT_NAME("(SELECT name FROM {resource}.c_storage_unit su WHERE su.id = %s LIMIT 1)"),
    // 获取仓库名称
    GET_WAREHOUSE_NAME("(SELECT name FROM {resource}.c_warehouse w WHERE w.id = %s LIMIT 1)"),
    // 统计总额
    STATISTICAL_GOODS_AMOUNT("(SELECT sum(price) * sum(number) FROM {resource}.`c_storage_transfer_goods` stg where stg.storage_transfer_id = st.id)"),
    // 获取设备实际周期
    GET_DEVICE_ACTUAL_CYCLE("(SELECT IF(deviceRealTimeData.actual_cycle > 0, deviceRealTimeData.actual_cycle, NULL) FROM lamp_ds_c_collector.device_realtime_data deviceRealTimeData WHERE deviceRealTimeData.ip = d.ip AND deviceRealTimeData.mac = d.mac)"),
    GET_DEVICE_CODE("(SELECT order_code FROM {resource}.c_order o WHERE o.id = %s)"),

    // 统计首检-正常项目数
    FIRST_CHECK_STATUS_NORMAL("(LENGTH(check_content) - LENGTH(REPLACE(check_content, '\"status\":\"正常\"', '')) ) / 17"),
    // 统计首检-异常项目数
    FIRST_CHECK_STATUS_ABNORMAL("(LENGTH(check_content) - LENGTH(REPLACE(check_content, '\"status\":\"异常\"', '')) ) / 17"),
    ;

    private SelectSQLExtendEnum(String str){
        desc = str;
    }

    private SelectSQLExtendEnum(String str, String annotate){
        desc = str;
        this.annotate = annotate;
    }

    private String desc;
    private String annotate;

    public static String translateSQL(SelectSQLExtendEnum enumItem, Field field, String fieldName , Class c){
        String sqlFormat = enumItem.getDesc();
        switch (enumItem){
            case DEFAULT_NUMBER_VALUE:
                break;
            case GROUP_CONCAT:
            case LAST_ONE_GROUP_CONCAT:
            case GET_DEVICE_ORIGINAL_PRODUCT:
                String tableNick = "";
                // 判断是否存在属性注解
                if(field.isAnnotationPresent(SQLTableNick.class)){
                    SQLTableNick sqlTableNick = field.getAnnotation(SQLTableNick.class);
                    tableNick = sqlTableNick.value();
                }
                // 判断是否存在类注解
                else if(c.isAnnotationPresent(SQLTableNick.class)){
                    SQLTableNick globalSQLTableNick = (SQLTableNick) c.getAnnotation(SQLTableNick.class);
                    tableNick = globalSQLTableNick.value();
                }
                // 提出表别名
                if(tableNick.contains(".")){
                    tableNick = tableNick.split("\\.")[0];
                }
                sqlFormat = sqlFormat.replace("{tableNick}", tableNick);
                break;
            case GET_STORAGE_COUNT:
                break;
        }
        // 替换数据源
//        sqlFormat = sqlFormat.replaceAll("\\{resource\\}",
//                "lamp_ds_c_base_" + ContextUtil.getTenantIdStr());
        return String.format(sqlFormat, fieldName);
    }



    public static String translateSQL2(SelectSQLExtendEnum enumItem, AKField field, String fieldName , AKClass c){
        String sqlFormat = enumItem.getDesc();
        switch (enumItem){
            case DEFAULT_NUMBER_VALUE:
                break;
            case GROUP_CONCAT:
            case LAST_ONE_GROUP_CONCAT:
            case GET_DEVICE_ORIGINAL_PRODUCT:
                String tableNick = "";
                // 判断是否存在属性注解
                if(field.isAnnotationPresent(SQLTableNick.class)){
//                    SQLTableNick sqlTableNick = field.getAnnotation(SQLTableNick.class);
                    String sqlTableNickValue = field.getAnnotation(SQLTableNick.class).toString();
//                    tableNick = sqlTableNick.value();
                    tableNick = sqlTableNickValue;
                }
                // 判断是否存在类注解
                else if(c.isAnnotationPresent(SQLTableNick.class)){
                    String globalSQLTableNick = (String)c.getAnnotation(SQLTableNick.class);
                    tableNick = globalSQLTableNick;
                }
                // 提出表别名
                if(tableNick.contains(".")){
                    tableNick = tableNick.split("\\.")[0];
                }
                sqlFormat = sqlFormat.replace("{tableNick}", tableNick);
                break;
            case GET_STORAGE_COUNT:
                break;
        }
        // 替换数据源
        sqlFormat = sqlFormat.replaceAll("\\{resource\\}",
                "wuyang_" + 1);
        return String.format(sqlFormat, fieldName);
    }


    /**
     * 变量补充selectSql
     * @param sql 原SQL
     * @param fieldList 需转换属性
     * @param dtoToMap 需转换值
     * @return
     */
    public static String extendSelectSql(String sql, List<Field> fieldList, Map dtoToMap){
        String newSQL = sql + "";

        Pattern pattern = Pattern.compile("\\{[^}]{0,}\\}",Pattern.MULTILINE);

        // 找出匹配变量, 转换为map对象
        // map结构 { "key":"",,"replaceKey":"{xxxx}", "tableNick": "xxx" }
        Matcher matcher = pattern.matcher(newSQL);
        List<Map<String, String>> matchList = new ArrayList<>();
        while (matcher.find()){
            String matchString = matcher.group();
            String matchStringKey = matchString.replace("{","").replace("}","");
            String[] variable = matchStringKey.split("\\.");
            Map<String, String> tempMap = new HashMap<>();
            if(variable.length == 2){
                tempMap.put("tableNick", variable[0]);
                tempMap.put("key", variable[1]);
            } else {
                tempMap.put("key", matchStringKey);
            }
            tempMap.put("replaceKey", matchStringKey);
            matchList.add(tempMap);
        }

        // 扩展定义变量转换
        Map<String, String> timeRange = new HashMap<>();
        for (Field field : fieldList) {
            Object nextDto = dtoToMap.get(field.getName());
            // 判断值是否为空
            if(Objects.isNull(nextDto)) continue;


            // 类型一: 时间
            if(field.isAnnotationPresent(SQLWhereTimeRange.class) || field.isAnnotationPresent(SQLWhereRange.class)){
                List<String> range = filteTimeRange(nextDto, field, timeRange);
                if(Objects.nonNull(range)){
                    String dateKey = field.getName().replace("End","").replace("Start","");

                    for (int i = 0; i < matchList.size(); i++) {
                        Map<String,String> matchMap = matchList.get(i);
                        String matchKey = matchMap.get("key");
                        if(matchKey.equals(dateKey)){
                            String keyToLine = (Objects.nonNull(matchMap.get("tableNick")) ? matchMap.get("tableNick") + "." : "") + StrUtil.toUnderlineCase(dateKey);
                            String itemSql = "(" + keyToLine + " BETWEEN \"" + range.get(0) + "\" AND " + " \"" + range.get(1) + "\")";
                            // 替换
                            newSQL = newSQL.replaceAll("\\{"+ matchMap.get("replaceKey") +"\\}", "AND " + itemSql);
                        }
                    }
                }
                continue;
            }
            // 其他类型: 替换对应
            else {
                String sqlSelectVariable = field.getAnnotation(SQLSelectExtendVariable.class).value();
                sqlSelectVariable = sqlSelectVariable.replaceAll("\\{"+field.getName()+"\\}", nextDto.toString());
                newSQL = newSQL.replaceAll("\\{"+field.getName()+"\\}", sqlSelectVariable);
            }
        }

        // 去掉多余变量
        for (Map<String, String> stringStringMap : matchList) {
            newSQL = newSQL.replaceAll("\\{" + stringStringMap.get("replaceKey") + "\\}", "");
        }


        return newSQL;
    }


    private static List<String> filteTimeRange(Object item, Field field, Map<String, String> timeRange){
        String key = field.getName();
        timeRange.put(key, item.toString());
        // 判断是否开始时间参数
        if(key.contains("Start")){
            String endKey = key.replace("Start","End");
            String endTime =timeRange.get(endKey);
            if(Objects.nonNull(endTime)){
                return Arrays.asList(item.toString(), endTime);
            }
        }
        // 判断是否结束时间参数
        else if(key.contains("End")){
            String startKey = key.replace("End","Start");
            String startTime =timeRange.get(startKey);
            if(Objects.nonNull(startTime)){
                return Arrays.asList(startTime, item.toString());
            }
        }

        return null;
    }
}
