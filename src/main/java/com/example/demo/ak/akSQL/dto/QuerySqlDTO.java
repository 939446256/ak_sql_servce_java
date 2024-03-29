package com.example.demo.ak.akSQL.dto;

import com.example.demo.ak.akSQL.AKSQLMapper;
import com.example.demo.ak.akSQL.annotation.SQLSelectCustom;
import com.example.demo.ak.akSQL.annotation.SQLSelectExtendVariable;
import com.example.demo.ak.akSQL.annotation.SQLSelectFieldNick;
import com.example.demo.ak.akSQL.core.*;
import lombok.Data;
import org.apache.commons.beanutils.BeanMap;
import com.example.demo.ak.akSQL.annotation.SQLAssociativeTable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * <p>
 * 实体类
 * </p>
 *
 * @author AK
 * @since 2022-06-16
 */
@Data
public class QuerySqlDTO {


    public AKSQLMapper sqlMapper;

    // 结果字段
    public Map<String, Object> resultNewMap;


    public QuerySqlDTO(){
    }

    public QuerySqlDTO(Class voClass){
        this(voClass, null);
    }
    public String getMainTable(Class c){
        SQLAssociativeTable associativeTable = (SQLAssociativeTable) c.getAnnotation(SQLAssociativeTable.class);
        String[] associativeTableList =  associativeTable.value();
        String[] tableNick = associativeTableList[0].split(" ");
        if(tableNick.length > 1){
            return tableNick[tableNick.length - 1];
        } else {
            return associativeTableList[0];
        }
    }
    public String getMainTable2(AKClass c){
//        SQLAssociativeTable associativeTable = (SQLAssociativeTable) c.getAnnotation(SQLAssociativeTable.class);
//        String[] associativeTableList =  associativeTable.value();
        List<String> associativeTableList = (List<String>) c.getAnnotation(SQLAssociativeTable.class);
        String[] tableNick = associativeTableList.get(0).split(" ");
        if(tableNick.length > 1){
            return tableNick[tableNick.length - 1];
        } else {
            return associativeTableList.get(0);
        }
    }

    public QuerySqlDTO(Class voClass, Object dto)  {
        this(voClass, dto, null);
    }

    public QuerySqlDTO(Class voClass, Object dto, String tenantId)  {
        // 生成SQL: 查询字段
        selectSql = SQLSelectTranslate.mapToSelectSql(voClass);
        // 生成SQL: 联表
        tableSql = SQLTableTranslate.listToTableSql(voClass);
        // 获取主表
        mainTable = getMainTable(voClass);
        // 生成SQL: 查询条件
        if(Objects.isNull(dto)){
            try { whereSql = SQLWhereTranslate.mapToQuerySql(voClass.getConstructor().newInstance(), voClass);}
            catch (Exception err){}
        } else {
            whereSql = SQLWhereTranslate.mapToQuerySql(dto, dto.getClass());
        }
        // 生成SQL: 追加
        addSql = SQLBaseTranslate.addToTableSql(voClass);

        // ====== 补充selectSql =======
        Map dtoToMap = new BeanMap(dto);
        Field[] list = Objects.isNull(dto) ?
                voClass.getDeclaredFields()
                : dto.getClass().getDeclaredFields();

        // 通过dto补充selectSql指查询条件
        if(Objects.nonNull(dto)){
            List<Field> fieldList = new ArrayList<>();
            // 取出对应字段名
            for (Field field : list){
                if(field.isAnnotationPresent(SQLSelectExtendVariable.class)){
                    fieldList.add(field);
                }
            }
            if(fieldList.size() > 0){
                selectSql = SQLSelectTranslate.extendSelectSql(selectSql, fieldList, dtoToMap);
            }
        }


        // ====== 补充selectSql =======
//        Map dtoToMap = new BeanMap(dto);
//        Field[] list = Objects.isNull(dto) ?
//                voClass.getDeclaredFields()
//                : dto.getClass().getDeclaredFields();
//
//        // 通过dto补充selectSql指查询条件
//        if(Objects.nonNull(dto)){
//            List<Field> fieldList = new ArrayList<>();
//            // 取出对应字段名
//            for (Field field : list){
//                if(field.isAnnotationPresent(SQLSelectExtendVariable.class)){
//                    fieldList.add(field);
//                }
//            }
//            if(fieldList.size() > 0){
//                selectSql = SQLSelectTranslate.extendSelectSql(selectSql, fieldList, dtoToMap);
//            }
//        }
//
//
//
//        // ========= 嵌套sql =========
//        // 判断是否有字段
//        List<Field> fieldList = new ArrayList<>();
//        // 取出对应字段名
//        for (Field field : list){
//            if(field.isAnnotationPresent(SQLEmbedTable.class)){
//                fieldList.add(field);
//            }
//        }
//        // 递归查询
//        if(fieldList.size() > 0){
//            for (int i = 0; i < fieldList.size(); i++) {
//                Field field = fieldList.get(i);
//                // 取对应对象
//                Object nextDto = dtoToMap.get(field.getName());
//                if(Objects.isNull(nextDto)){
//                    try {
//                        field.getType().getConstructor().newInstance();
//                    }catch (Exception err){
//                        throw new RuntimeException(err.getMessage());
//                    }
//                }
//                QuerySqlDTO mergeTable = new QuerySqlDTO(voClass, nextDto);
//                String mergeSQL = " SELECT "+ mergeTable.selectSql +" FROM " + mergeTable.tableSql;
//                if(StringUtils.hasText(mergeTable.whereSql)){
//                    mergeSQL += " where " + mergeTable.whereSql;
//                }
//                mergeSQL += mergeTable.addSql;
//                // 重新当前属性
//                selectSql = "*";
//                tableSql = "(" + mergeSQL + ") " + field.getName();
//                addSql = "";
//            }
//
//        }

    }


    public QuerySqlDTO(AKClass akClass, AKObject akObject, AKSQLMapper sqlMapper)  {

        resultNewMap = new HashMap<>();
        // 特殊处理: 字段别名
        for (AKField akField : akClass.getField()) {
            String fieldNick = (String) akField.getAnnotation(SQLSelectFieldNick.class);
            if(Objects.isNull(fieldNick)){
                resultNewMap.put(akField.getName(), null);
                continue;
            }
            resultNewMap.put(fieldNick, null);
            akField.setAnnotation(SQLSelectCustom.class, akField.getName() + " as " + fieldNick);
        }

        // 更新连表
        List<List<String>> relationList = new ArrayList<>();
        List<AssociativeTableDTO> associativeTableList = sqlMapper.queryForList(AssociativeTableDTO.class);
        for (AssociativeTableDTO associativeTableDTO : associativeTableList) {
            List<String> list = new ArrayList<>();
            list.add(associativeTableDTO.getTableFirst());
            list.add(associativeTableDTO.getAssociativeFieldFirst());
            list.add(associativeTableDTO.getTableSecond());
            list.add(associativeTableDTO.getAssociativeFieldSecond());
            relationList.add(list);
        }
        AKSQLTableTranslate.relationList = relationList;
        // 生成SQL: 查询字段
        selectSql = AKSQLSelectTranslate.mapToSelectSql(akClass);
        // 生成SQL: 联表
        tableSql = AKSQLTableTranslate.listToTableSql(akObject, akClass);
        // 获取主表
        mainTable = getMainTable2(akClass);
        // 生成SQL: 查询条件
//        if(Objects.isNull(dto)){
//            try { whereSql = SQLWhereTranslate.mapToQuerySql(voClass.getConstructor().newInstance(), voClass);}
//            catch (Exception err){}
//        } else {
//            whereSql = AKSQLWhereTranslate.mapToQuerySql(akObject, akClass);
//        }
        // 生成SQL: 查询条件
        whereSql = AKSQLWhereTranslate.mapToQuerySql(akObject, akClass);
        // 生成SQL: 追加
        addSql = AKSQLBaseTranslate.addToTableSql(akClass);

        // ====== 补充selectSql =======
//        Map dtoToMap = new BeanMap(dto);
//        Field[] list = Objects.isNull(dto) ?
//                voClass.getDeclaredFields()
//                : dto.getClass().getDeclaredFields();
//
//        // 通过dto补充selectSql指查询条件
//        if(Objects.nonNull(dto)){
//            List<Field> fieldList = new ArrayList<>();
//            // 取出对应字段名
//            for (Field field : list){
//                if(field.isAnnotationPresent(SQLSelectExtendVariable.class)){
//                    fieldList.add(field);
//                }
//            }
//            if(fieldList.size() > 0){
//                selectSql = SQLSelectTranslate.extendSelectSql(selectSql, fieldList, dtoToMap);
//            }
//        }
//
//
//
//        // ========= 嵌套sql =========
//        // 判断是否有字段
//        List<Field> fieldList = new ArrayList<>();
//        // 取出对应字段名
//        for (Field field : list){
//            if(field.isAnnotationPresent(SQLEmbedTable.class)){
//                fieldList.add(field);
//            }
//        }
//        // 递归查询
//        if(fieldList.size() > 0){
//            for (int i = 0; i < fieldList.size(); i++) {
//                Field field = fieldList.get(i);
//                // 取对应对象
//                Object nextDto = dtoToMap.get(field.getName());
//                if(Objects.isNull(nextDto)){
//                    try {
//                        field.getType().getConstructor().newInstance();
//                    }catch (Exception err){
//                        throw new RuntimeException(err.getMessage());
//                    }
//                }
//                QuerySqlDTO mergeTable = new QuerySqlDTO(voClass, nextDto);
//                String mergeSQL = " SELECT "+ mergeTable.selectSql +" FROM " + mergeTable.tableSql;
//                if(StringUtils.hasText(mergeTable.whereSql)){
//                    mergeSQL += " where " + mergeTable.whereSql;
//                }
//                mergeSQL += mergeTable.addSql;
//                // 重新当前属性
//                selectSql = "*";
//                tableSql = "(" + mergeSQL + ") " + field.getName();
//                addSql = "";
//            }
//
//        }

    }


    private Long id;

    // 查询字段
    private String selectSql;

    // 表单
    private String tableSql = "";

    // 过滤条件
    private String whereSql;

    // 追加
    private String addSql = "";

    // 主表
    private String mainTable = "";




    /**
     * 缓存参数
     */

    private String baseTableKey;

    private String lastTableKey;

    private String lastTablename;

    /**
     * 例子:
     *        querySqlDTO
     *                .baseTable("c_achievement_indicators").nick("ai")
     *                .addTable("c_achievement_indicators_detail").nick("aid").onKey("achievement_indicators_id")
     *                .setBaseKey("ai.archive_id")
     *                .addTable("c_company_archive").nick("ca").onKey("id")
     *                .addTable("c_incubation_archive").nick("ia").onKey("archive_id");
     */

    public QuerySqlDTO baseTable(String tableName){
        baseTable(tableName, "id");
        return this;
    }

    public QuerySqlDTO baseTable(String tableName,String baseTableKey){
        tableSql = tableName;
        lastTablename = tableName;
        this.baseTableKey = baseTableKey;
        lastTableKey = tableName + "." + baseTableKey;
        return this;
    }

    public QuerySqlDTO addTable(String tableName){
        tableSql += " LEFT JOIN " + tableName;
        lastTablename = tableName;
        return this;
    }

    public QuerySqlDTO nick(String tableNick){
        if(!tableSql.contains("LEFT JOIN")){
            // 替换主表key
            lastTableKey = tableNick + "." + baseTableKey;
        }
        tableSql += " " + tableNick;
        lastTablename = tableNick;
        return this;
    }

    public QuerySqlDTO onKey(String key){
        tableSql += " ON " + lastTablename + "." + key + " = " + lastTableKey;
        return this;
    }

    public QuerySqlDTO setBaseKey(String key){
        lastTableKey = key;
        return this;
    }


}
