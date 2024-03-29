package com.example.demo.ak.akSQL;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.ak.akSQL.annotation.*;
import com.example.demo.ak.akSQL.core.AKSQLBaseTranslate;
import com.example.demo.ak.akSQL.dto.*;
import com.example.demo.ak.akSQL.enums.OrderEnum;
import com.example.demo.ak.akSQL.mapper.BaseSQLMapper;
import com.example.demo.ak.akSQL.vo.ContextUtil;
import com.example.demo.config.AKTool;
import com.example.demo.dto.AKFeildPageParams;
import com.example.demo.dto.AKFeildParams;
import com.example.demo.config.AKFeildParamsInterface;
import com.example.demo.dto.AKIPage;
import com.example.demo.server.AdminService;
import org.apache.commons.beanutils.BeanMap;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.ak.akSQL.core.SQLBaseTranslate;
import com.example.demo.ak.akSQL.core.SQLTableTranslate;
import com.example.demo.ak.akSQL.core.SQLWhereTranslate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AKSQLMapper {

    @Autowired
    private BaseSQLMapper baseSQLMapper;

    @Autowired(required=false)
    private SqlSessionTemplate sqlSessionTemplate;

    public Boolean insertTable(InsertSqlDTO example){
        return baseSQLMapper.insertTable(example);
    }

    private String packageString = null;

    @Autowired
    private AdminService adminService;
    @Autowired
    private AKTool tool;


    /**
     * 新增/修改SQL数据
     * @param dto 请求对象
     * @return
     */
    public Boolean insertAndUpdateTable(Object dto){
        Map dtoMap = new BeanMap(dto);
        Boolean hasId = Objects.nonNull(dtoMap.get("id"))
                && !StringUtils.isEmpty(dtoMap.get("id").toString());
        return hasId ?
                this.updateTable(dto)
                : this.insertTable(dto);
    }

    /**
     * 新增/修改SQL数据
     * @return
     */
    public String insertAndUpdateTableAK(String tableName, Map dtoMap){
        Boolean hasId = Objects.nonNull(dtoMap.get("id"))
                && !StringUtils.isEmpty(dtoMap.get("id").toString());
        return hasId ?
                this.updateTableAK(tableName, dtoMap)
                : this.insertTableAK(tableName, dtoMap);
    }



    /**
     * 新增SQL数据
     * @param dto 请求对象
     * @return
     */
    public Boolean insertTable(Object dto){
        return insertTable(dto, null);
    }
    /**
     * 新增SQL数据
     * @param dto 请求对象
     * @return
     */
    public String insertTableAK(String tableName, Map dto){
        return insertTableAK(tableName, dto, null);
    }

    /**
     * 新增SQL数据
     * @param dto 请求对象
     * @return
     */
    public Boolean insertTable(Object dto, String tenantId){
        // 获取声明中的表名称
        String tableName = SQLTableTranslate.listToTableSql(dto.getClass(), tenantId);
        // 兼容视图多数据源或起别名的对象
        String[] tableNameList = tableName.split(",");
        tableNameList = tableNameList[0].split(" ");
        tableName = tableNameList[0];
        InsertSqlDTO insertSqlDTO = new InsertSqlDTO(tableName,dto);

//        // 请求人ID
//        Long employeeId = ContextUtil.getEmployeeId();
//        // 添加创建人/修改人
//        if(!insertSqlDTO.getSql().contains("created_by")){
//            insertSqlDTO.setSql(insertSqlDTO.getSql() + ", created_by = " + employeeId);
//        }
//        if(!insertSqlDTO.getSql().contains("updated_by")){
//            insertSqlDTO.setSql(insertSqlDTO.getSql() + ", updated_by = " + employeeId);
//        }
        insertSqlDTO.setSql(insertSqlDTO.getSql());
        Boolean result = baseSQLMapper.insertTable(insertSqlDTO);
        // 赋值ID到dto
        BeanUtil.copyProperties( insertSqlDTO, dto);
        return result;
    }


    /**
     * 新增SQL数据
     * @param dto 请求对象
     * @return
     */
    public String insertTableAK(String tableName, Map dto, String tenantId){
        // 获取声明中的表名称
//        String tableName = SQLTableTranslate.listToTableSql(dto.getClass(), tenantId);
//        // 兼容视图多数据源或起别名的对象
//        String[] tableNameList = tableName.split(",");
//        tableNameList = tableNameList[0].split(" ");
//        tableName = tableNameList[0];
        List<String> sqlList = new ArrayList<>();

        // 获取表对应字段
        List<Map> feildList = baseSQLMapper.getAllFieldsForTables(tableName);
        List<String> feildNameList = feildList.stream().map(item -> item.get("Field").toString()).collect(Collectors.toList());

        for (Object o : dto.keySet()) {
            String key = o.toString();
            if(key.equals("id")) continue;
            String sqlFeild = SQLBaseTranslate.humpToLine(key.toString());
            if(!feildNameList.contains(sqlFeild)) continue;
            if(Objects.isNull(dto.get(key)) || StringUtils.isEmpty(dto.get(key).toString())) continue;
            // 判断是否时间戳
            boolean isTimestamp = Pattern.matches("^\\d{13}$", dto.get(key).toString());
            if(isTimestamp){
                String itemSql = sqlFeild + "= FROM_UNIXTIME( " + dto.get(key).toString()  + " div 1000 )";
                sqlList.add(itemSql);
                continue;
            }

            // 判断是否布尔类型
            String itemSql;
            if(dto.get(key) instanceof Boolean){
                itemSql = sqlFeild + "=" + dto.get(key).toString();
            } else {
                itemSql = sqlFeild + "=\"" + dto.get(key).toString() + "\"";
            }
            sqlList.add(itemSql);
        }
        InsertSqlDTO insertSqlDTO = new InsertSqlDTO();
        insertSqlDTO.setTableName(tableName);
        insertSqlDTO.setSql(String.join(",", sqlList));


        // 请求人ID
        Long userId = ContextUtil.getUserId();
        // 添加创建人/修改人
        if(!insertSqlDTO.getSql().contains("created_by")){
            insertSqlDTO.setSql(insertSqlDTO.getSql() + ", created_by = " + userId);
        }
        if(!insertSqlDTO.getSql().contains("updated_by")){
            insertSqlDTO.setSql(insertSqlDTO.getSql() + ", updated_by = " + userId);
        }
        insertSqlDTO.setSql(insertSqlDTO.getSql());
        Boolean result = baseSQLMapper.insertTable(insertSqlDTO);
        // 赋值ID到dto
//        BeanUtil.copyProperties( insertSqlDTO, dto);
        return insertSqlDTO.getId().toString();
    }

    /**
     * 修改SQL修改
     * @param dto 请求参数
     * @return
     */
    public Boolean updateTable(Object dto){
        // 获取声明中的表名称
        String tableName = SQLTableTranslate.listToTableSql(dto.getClass());

        // 兼容视图多数据源或起别名的对象
        String[] tableNameList = tableName.split(",");
        tableNameList = tableNameList[0].split(" ");
        tableName = tableNameList[0];
        UpdateSqlDTO updateSqlDTO = new UpdateSqlDTO(tableName,dto);

//        // 请求人ID
//        Long employeeId = ContextUtil.getEmployeeId();
//        // 添加创建人/修改人
//        updateSqlDTO.setUpdateSQL(updateSqlDTO.getUpdateSQL() + ", updated_by = " + employeeId);
        return baseSQLMapper.updateTable(updateSqlDTO);
    }


    /**
     * 修改SQL修改
     * @param dto 请求参数
     * @return
     */
    public String updateTableAK(String tableName, Map dto){
        // 获取声明中的表名称
        List<String> sqlList = new ArrayList<>();

        // 获取表对应字段
        List<Map> feildList = baseSQLMapper.getAllFieldsForTables(tableName);
        List<String> feildNameList = feildList.stream().map(item -> item.get("Field").toString()).collect(Collectors.toList());

        for (Object o : dto.keySet()) {
            String key = o.toString();
            String sqlFeild = SQLBaseTranslate.humpToLine(key.toString());
            // 特殊处理忽略时间
            if(sqlFeild.equals("created_time")) continue;
            if(sqlFeild.equals("updated_time")) continue;
            if(!feildNameList.contains(sqlFeild)) continue;
            if(Objects.isNull(dto.get(key)) || StringUtils.isEmpty(dto.get(key).toString())) continue;
            // 判断是否时间戳
            boolean isTimestamp = Pattern.matches("^\\d{13}$", dto.get(key).toString());
            if(isTimestamp){
                String itemSql = sqlFeild + "= FROM_UNIXTIME( " + dto.get(key).toString()  + " div 1000 )";
                sqlList.add(itemSql);
                continue;
            }

            // 判断是否布尔类型
            String itemSql;
            if(dto.get(key) instanceof Boolean){
                itemSql = sqlFeild + "=" + dto.get(key).toString();
            } else {
                itemSql = sqlFeild + "=\"" + dto.get(key).toString() + "\"";
            }
            sqlList.add(itemSql);
        }
        UpdateSqlDTO updateSqlDTO = new UpdateSqlDTO();
        updateSqlDTO.setTableName(tableName);
        updateSqlDTO.setUpdateSQL(String.join(",", sqlList));
        updateSqlDTO.setWhereSQL("id = " + dto.get("id"));
        // 请求人ID
        Long userId = ContextUtil.getUserId();
        // 添加创建人/修改人
        updateSqlDTO.setUpdateSQL(updateSqlDTO.getUpdateSQL() + ", updated_by = " + userId);
        baseSQLMapper.updateTable(updateSqlDTO);
        return dto.get("id").toString();
    }


    // ===========================  详情_请求方法 ===============================
    /**
     * 请求列表
     * @param dto 请求参数
     * @return
     */
    public  <T> T getDetail(Object dto){
        Class c = dto.getClass();
        return getDetail(dto, c, null);
    }

    /**
     * 请求列表
     * @param dto 请求参数
     * @return
     */
    public  <T> T getDetail(Object dto,String tenantId){
        Class c = dto.getClass();
        return getDetail(dto, c, tenantId);
    }

    /**
     * 请求列表
     * @param dto 请求参数
     * @return
     */
    public  <T> T getDetail(Object dto, Class c){
        return getDetail(dto, c, null);
    }

    /**
     * 请求列表
     * @param dto 请求参数
     * @return
     */
    public  <T> T getDetail(Object dto, Class c, String tenantId){
        QuerySqlDTO querySqlDTO = new QuerySqlDTO(c, dto, tenantId);
        if(StringUtils.isEmpty(querySqlDTO.getWhereSql())){
            return null;
        }
        // 判断是否有SQLEnd注解的addSql, 没有则默认取最近一条数据
        if(StringUtils.isEmpty(querySqlDTO.getAddSql())){
            String mainTable = querySqlDTO.getMainTable();
            if(!StringUtils.isEmpty(mainTable)) {
                mainTable += ".";
            }
            // 添加限制
            querySqlDTO.setAddSql(" ORDER BY " + mainTable + "created_time DESC ");
            Boolean hasCollection = false;
            for (Field declaredField : c.getDeclaredFields()) {
                if(declaredField.isAnnotationPresent(SQLSelectCollection.class)) {hasCollection = true;break;}
            }
            if(hasCollection.equals(false)) querySqlDTO.setAddSql(querySqlDTO.getAddSql() + " LIMIT 1");
        }
        List<T>  list = queryForList(querySqlDTO, c);
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * 请求列表
     * @return
     */
    public  Map<String, Object> getDetailAK(AKFeildParams feildParams) throws IOException {

        // 转换AKSQL对象
        QuerySqlDTO querySqlDTO = adminService.simulateRequestParams(feildParams);
        if(StringUtils.isEmpty(querySqlDTO.getWhereSql())){
            return null;
        }
        // 判断是否有SQLEnd注解的addSql, 没有则默认取最近一条数据
        String mainTable = querySqlDTO.getMainTable();
        querySqlDTO.setAddSql(" ORDER BY " + mainTable + ".created_time DESC ");
        querySqlDTO.setAddSql(querySqlDTO.getAddSql() + " LIMIT 1");

        List<Map<String, Object>>  list = queryForCollectListAK(querySqlDTO, feildParams);
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * 查询数量
     * @param dto 请求对象
     * @return
     */
    public Integer getCount(Object dto){
        Class c = dto.getClass();
        QuerySqlDTO querySqlDTO = new QuerySqlDTO(c, dto);
        if(StringUtils.isEmpty(querySqlDTO.getWhereSql())){
            return Integer.valueOf(0);
        }
        Integer count = baseSQLMapper.getCount(querySqlDTO);
        return Objects.isNull(count) ? Integer.valueOf(0) : count;
    }
    public Integer getCountAK(AKFeildParams feildParams){
        QuerySqlDTO querySqlDTO = adminService.simulateRequestParams(feildParams);
        if(StringUtils.isEmpty(querySqlDTO.getWhereSql())){
            return Integer.valueOf(0);
        }
        Integer count = baseSQLMapper.getCount(querySqlDTO);
        return Objects.isNull(count) ? Integer.valueOf(0) : count;
    }

    /**
     * 是否有值
     * @param dto 请求对象
     * @return
     */
    public Boolean hasData(Object dto){
        Class c = dto.getClass();
        QuerySqlDTO querySqlDTO = new QuerySqlDTO(c, dto);
        if(StringUtils.isEmpty(querySqlDTO.getWhereSql())){
            return null;
        }
        Integer count = baseSQLMapper.getCount(querySqlDTO);
        return count > 0;
    }

    public Boolean hasData(AKFeildParams feildParams){
        // 转换AKSQL对象
        QuerySqlDTO querySqlDTO = adminService.simulateRequestParams(feildParams);
        if(StringUtils.isEmpty(querySqlDTO.getWhereSql())){
            return null;
        }
        Integer count = baseSQLMapper.getCount(querySqlDTO);
        return count > 0;
    }

    /**
     * 是否有值
     * @param dto 请求对象
     * @return
     */
    public Boolean hasData(Object dto, String tenantId){
        Class c = dto.getClass();
        QuerySqlDTO querySqlDTO = new QuerySqlDTO(c, dto, tenantId);
        if(StringUtils.isEmpty(querySqlDTO.getWhereSql())){
            return null;
        }
        Integer count = baseSQLMapper.getCount(querySqlDTO);
        return count > 0;
    }


    /**
     * 请求列表
     * @return
     */
    public List<String> showTable(){
        return baseSQLMapper.showTable();
    }
    public String getTableCommit(String tableName){
        return baseSQLMapper.getTableCommit(tableName);
    }

    /**
     * 请求列表
     * @return
     */
    public List<Map> getAllFieldsForTables(String table){
        return baseSQLMapper.getAllFieldsForTables(table);
    }

    // ===========================  列表_请求方法 ===============================

    /**
     * 请求列表
     * @param c 返回类型
     * @return
     */
    public <T> List<T> queryForList(Class<T> c){
        QuerySqlDTO querySqlDTO = new QuerySqlDTO(c);
        return queryForList(querySqlDTO, c);
    }
    /**
     * 请求列表
     * @param c 返回类型
     * @return
     */
    public <T> List<T> queryForList(Class<T> c, String tenantId){
        QuerySqlDTO querySqlDTO = new QuerySqlDTO(c, null, tenantId);
        return queryForList(querySqlDTO, c);
    }

    /**
     * 请求列表
     * @return
     */
    public <T> List<T> queryForList(Object dto){
        return (List<T>) queryForList(dto, dto.getClass());
    }


    /**
     * 请求列表
     * @return
     */
    public <T> List<T> queryForList(Object dto, String tenantId){
        return (List<T>) queryForList(dto, dto.getClass(), tenantId);
    }

    /**
     * 请求列表
     * @param dto 筛选参数
     * @param c 返回类型
     * @return
     */
    public <T> List<T> queryForList(Object dto, Class<T> c)  {
        return (List<T>) queryForList(dto, c, null);
    }

    /**
     * 请求列表
     * @param dto 筛选参数
     * @param c 返回类型
     * @return
     */
    public <T> List<T> queryForList(Object dto, Class<T> c,String tenantId)  {
        // 转SQL
        QuerySqlDTO querySqlDTO = new QuerySqlDTO(c, dto, tenantId);
        return queryForList(querySqlDTO, c);
    }

    // ===========================  分页_请求方法 ===============================



    public AKIPage<Map> queryForPageListAK(AKFeildPageParams feildParams) throws Exception {
        AKPageParams<Map> params = feildParams.getRequestParams();
        // 转换AKSQL对象
        QuerySqlDTO querySqlDTO = adminService.simulateRequestPageParams(feildParams);

        AKPageParams<QuerySqlDTO> newParams = BeanUtil.copyProperties(params, AKPageParams.class);
        newParams.setModel(querySqlDTO);
        // 构造分页对象
        AKIPage<Map> iPage = params.buildPage();

        // 请求SQL
        int count = baseSQLMapper.queryForPageCount(newParams);
        if(count > 0){
            AKPageParams akPageParams = new AKPageParams();
            OrderEnum orderEnum = OrderEnum.get(params.getOrder());
            akPageParams.setOrder("ORDER BY " + params.getSort() + " " + orderEnum.getCode());
            akPageParams.setOffset(iPage.offset());
            akPageParams.setSize(iPage.getSize());
            List<Map> list = baseSQLMapper.queryForPageList2(akPageParams, newParams.getModel());

            // sql特定时间格式转换为时间戳
            translateTime(feildParams, list);

            iPage.setRecords(list);
            // 填充空字段
            fillEmptyFields(iPage.getRecords(), feildParams);

            // 注入SQLSelectCollection标签
            addPageCollect( feildParams, iPage.getRecords());
        } else {
            iPage.setRecords(new ArrayList<>());
        }

        iPage.setSize(params.getSize());
        iPage.setTotal(count);


        return iPage;
    }

    // sql特定时间格式转换为时间戳
    private void translateTime(AKFeildParamsInterface feildParams, Map record){
        translateTime(feildParams, Arrays.asList(record));
    }
    // sql特定时间格式转换为时间戳
    private void translateTime(AKFeildParamsInterface feildParams, List<Map> list){
        // 获取所有时间戳类型
        Map<String,String> typeMap = new HashMap<>();
        for (AKField akField : feildParams.getFieldList()) {
            String name =  akField.getName();
            if(!Objects.isNull(akField.getAnnotation(SQLSelectCustom.class))) {
                String selectCustom = akField.getAnnotation(SQLSelectCustom.class).toString();
                if(!Objects.isNull(selectCustom)){
                    name = selectCustom.split(" as ")[0];
                }
            }
            typeMap.put(AKSQLBaseTranslate.underlineToCamel(name), akField.getTypeName());
        }

        // 特殊处理时间类型
        for (Map map : list) {
            map.keySet().forEach((key)->{
                if(!Objects.isNull(typeMap.get(key))){
                    Object value = map.get(key);
                    if(!Objects.isNull(value)){
                        switch (typeMap.get(key)){
                            case "date":
                                map.put(key, ((Date) value).getTime());
                                break;
                            case "datetime":
                                map.put(key, ((Timestamp) value).getTime());
                                break;
                            case "bigint":
                                map.put(key, ((Long) value).toString());
                                break;
                        }
                    }
                }
            });
        }
    }

    private void addPageCollect( AKFeildPageParams feildParams, List<Map> recordList) throws IOException {
        List<AKField> collectionFeildList = new ArrayList<>();
        for (AKField declaredField : feildParams.getFieldList()) {
            if(declaredField.isAnnotationPresent(SQLSelectCollection.class)) {
                collectionFeildList.add(declaredField);
            }
        }
        if(collectionFeildList.size() > 0){
            for (Map record : recordList) {
                for (AKField collectionFeild : collectionFeildList) {
                    // 采集下查询接口
                    AKFeildParams collectFeildParams = tool.getIOParams(collectionFeild.getAnnotation(SQLSelectCollection.class).toString(), new HashMap<>());
                    // 添加主表
                    JSONArray collectAssociativeTable = (JSONArray) collectFeildParams.getAnnotation().get("SQLAssociativeTable");
                    String mainAssociativeTable = ((JSONArray) feildParams.getAnnotation().get("SQLAssociativeTable")).get(0).toString();
                    String mainAssociativeTableNick = mainAssociativeTable.split(" ")[1];
                    collectAssociativeTable.add(0, mainAssociativeTable);

                    // 闭包防循环
                    collectFeildParams.setHasCollect(Boolean.TRUE);

                    // 添加主表ID
                    AKField mainField = new AKField();
                    mainField.setName("akCollectId");
                    mainField.setAnnotation(SQLTableNick.class, mainAssociativeTableNick);
                    mainField.setAnnotation(SQLSelectCustom.class, "id as akCollectId");
                    mainField.setAnnotation(SQLWhereEqual.class, "");
                    // 添加关联表字段
                    collectFeildParams.getFieldList().add(mainField);
                    // 添加查询主表id
                    Map body = new HashMap();
                    body.put("akCollectId", record.get("id"));
                    collectFeildParams.setRequestParams(body);

                    // 转换AKSQL对象
                    QuerySqlDTO collectQuerySqlDTO = adminService.simulateRequestParams(collectFeildParams);
                    record.put(collectionFeild.getName(), queryForCollectListAK( collectQuerySqlDTO, collectFeildParams));
                }
            }
        }
    }


    private void addCollect( AKFeildParams feildParams, List<Map<String, Object>> recordList) throws IOException {
        List<AKField> collectionFeildList = new ArrayList<>();
        for (AKField declaredField : feildParams.getFieldList()) {
            if(declaredField.isAnnotationPresent(SQLSelectCollection.class)) {
                collectionFeildList.add(declaredField);
            }
        }
        if(collectionFeildList.size() > 0){
            // 处理掉上一次关联的临时表
            for (AKField collectionFeild : collectionFeildList) {
                // 采集下查询接口
                AKFeildParams collectFeildParams = tool.getIOParams(collectionFeild.getAnnotation(SQLSelectCollection.class).toString(), new HashMap<>());
                // 是否已经注入过, 删除第一个关联表
                if(feildParams.getHasCollect() ){
                    ((JSONArray) feildParams.getAnnotation().get("SQLAssociativeTable")).remove(0);
                }
            }

            // 遍历数据列表和SQLSelectCollection字段列表,  利用数据ID查询关联接口
            for (Map record : recordList) {
                // 关联字段列表
                for (AKField collectionFeild : collectionFeildList) {
                    // 1.构造 collectFeildParams 查询对象
                    // 1.1 采集下查询接口
                    AKFeildParams collectFeildParams = tool.getIOParams(collectionFeild.getAnnotation(SQLSelectCollection.class).toString(), new HashMap<>());
                    // 1.2 添加主表
                    JSONArray collectAssociativeTable = (JSONArray) collectFeildParams.getAnnotation().get("SQLAssociativeTable");
                    // 1.3 获取接口第一个表为主表
                    String mainAssociativeTable = ((JSONArray) feildParams.getAnnotation().get("SQLAssociativeTable")).get(0).toString();
                    // 1.4 获取表昵称
                    String mainAssociativeTableNick = "";
                    if(mainAssociativeTable.split(" ").length > 1){
                        mainAssociativeTableNick = mainAssociativeTable.split(" ")[1];
                    } else {
                        mainAssociativeTableNick = mainAssociativeTable;
                    }
                    collectAssociativeTable.add(0, mainAssociativeTable);
                    // 闭包防循环
                    collectFeildParams.setHasCollect(Boolean.TRUE);

                    // 添加 关联ID
                    AKField mainField = new AKField();
                    mainField.setName("akCollectId");
                    mainField.setAnnotation(SQLTableNick.class, mainAssociativeTableNick);
                    mainField.setAnnotation(SQLSelectCustom.class, "id as akCollectId");
                    mainField.setAnnotation(SQLWhereEqual.class, "");
                    collectFeildParams.getFieldList().add(mainField);

                    Map body = new HashMap();
                    body.put("akCollectId", record.get("id"));
                    collectFeildParams.setRequestParams(body);

                    // 转换AKSQL对象
                    QuerySqlDTO collectQuerySqlDTO = adminService.simulateRequestParams(collectFeildParams);
                    // 查询对应列表
                    record.put(collectionFeild.getName(), queryForCollectListAK( collectQuerySqlDTO, collectFeildParams));
                }

            }
        }
    }


    /**
     * 批量更新
     * @param ids 对象ID
     * @param dto 更新内容
     * @return
     */
    public Boolean batchUpdate(List<Long> ids, Object dto){
        String table = SQLTableTranslate.listToTableSql(dto.getClass());
        // 只取第一个表
        String[] tables = table.split(" ");
        UpdateSqlDTO updateSqlDTO = new UpdateSqlDTO(tables[0],dto);
        return baseSQLMapper.batchUpdate(updateSqlDTO, ids);
    }

    /**
     * 批量删除 (假删除: deleted字段改为true)
     * @param ids 删除ID
     * @param c 数据对象
     * @return
     */
    public Boolean batchDelete(List<Long> ids, Class c){
        String table = SQLTableTranslate.listToTableSql(c);
        // 只取第一个表
        String[] tables = table.split(" ");
        return baseSQLMapper.batchSoftDelete(tables[0], ids);
    }

    /**
     * 批量删除 (假删除: deleted字段改为true)
     * @param ids 删除ID
     * @return
     */
    public Boolean batchDeleteAK(String tableName, List<Long> ids){
        return baseSQLMapper.batchSoftDelete(tableName, ids);
    }


    /**
     * 删除 (真删除: 从表中删除)
     * @return
     */
    public Boolean deleteFromTableAK(AKFeildParams feildParams, QuerySqlDTO querySqlDTO){
        String whereSql = querySqlDTO.getWhereSql();
        JSONArray SQLAssociativeTable = (JSONArray) feildParams.getAnnotation().get("SQLAssociativeTable");
        String tableName = SQLAssociativeTable.get(0).toString();
        if(tableName.contains(" ")){
            tableName = tableName.split(" ")[0];
        }
        return baseSQLMapper.deleteFromTable(tableName, whereSql);
    }


    /**
     * 删除 (真删除: 从表中删除)
     * @param dto 筛选条件
     * @return
     */
    public Boolean deleteFromTable(Object dto){
        String table = SQLTableTranslate.listToTableSql(dto.getClass());
        // 只取第一个表
        String[] tables = table.split(" ");
        String whereSql = SQLWhereTranslate.mapToQuerySql(dto, dto.getClass(), false);
        return baseSQLMapper.deleteFromTable(tables[0], whereSql);
    }




    // ===========================  封装方法 ===============================

    /**
     * 请求列表
     * @param querySqlDTO 转换后SQL语句
     * @param c 返回类型
     * @return
     */
    private <T> List<T> queryForList(QuerySqlDTO querySqlDTO, Class<T> c){
        List<Map<String, Object>> list = baseSQLMapper.queryForList(querySqlDTO);
        List<T> objectList = new ArrayList<>();
        for (Object item:list){
            Object obj = JSONObject.parseObject(JSONObject.toJSONString(item), c);
            objectList.add((T) obj);
        }
        return objectList;
    }
    public  <T> List<Map<String, Object>> queryForListAK( AKFeildParams feildParams){
        QuerySqlDTO querySqlDTO = adminService.simulateRequestParams(feildParams);
        List<Map<String, Object>> list = baseSQLMapper.queryForList(querySqlDTO);


        // 获取所有时间戳类型
        Map<String,String> typeMap = new HashMap<>();
        for (AKField akField : feildParams.getFieldList()) {
            String name =  akField.getName();  if(!Objects.isNull(akField.getAnnotation(SQLSelectCustom.class))) {
                String selectCustom = akField.getAnnotation(SQLSelectCustom.class).toString();
                if(!Objects.isNull(selectCustom)){
                    name = selectCustom.split(" as ")[0];
                }
            }
            typeMap.put(name, akField.getTypeName());
        }

        // 填充空字段
        for (Map record : list) {
            // 字段:判断是否有别名
            List<String> objKeyList = feildParams.getFieldList().stream().map(item-> {
                String sqlSelectCustomAnn = (String) item.getAnnotation().get("SQLSelectCustom");
                if(!Objects.isNull(sqlSelectCustomAnn) && sqlSelectCustomAnn.contains(" as ")){
                    String[] prefixSplitList = sqlSelectCustomAnn.split(" as ");
                    return AKSQLBaseTranslate.underlineToCamel(prefixSplitList[1]);
                }
                return AKSQLBaseTranslate.underlineToCamel(item.getName());
            }).collect(Collectors.toList());
            // 找出不存在字段
            objKeyList.removeAll(record.keySet());
            // 添加空字符串
            for (String nullKey : objKeyList) {
                record.put(nullKey, null);
            }

            // 特殊处理Long字段, 防止长度超出前端number类型限制
            for (Object key : record.keySet()) {
                String keyStr = key.toString();
                Object value = record.get(keyStr);

                if(value instanceof Long) {
                    record.put(keyStr, value.toString());
                }
            }

            // sql特定时间格式转换为时间戳
            translateTime(feildParams, record);
        }
        return list;
    }


    public  <T> List<Map<String, Object>> queryForCollectListAK( QuerySqlDTO querySqlDTO, AKFeildParams feildParams) throws IOException {
        List<Map<String, Object>> list = baseSQLMapper.queryForList(querySqlDTO);

        // 获取所有时间戳类型
        Map<String,String> typeMap = new HashMap<>();
        for (AKField akField : feildParams.getFieldList()) {
            String name =  akField.getName();
            if(!Objects.isNull(akField.getAnnotation(SQLSelectCustom.class))) {
                String selectCustom = akField.getAnnotation(SQLSelectCustom.class).toString();
                if(!Objects.isNull(selectCustom)){
                    name = selectCustom.split(" as ")[0];
                }
            }
            typeMap.put(AKSQLBaseTranslate.underlineToCamel(name), akField.getTypeName());
        }
        // 填充空字段
        for (Map record : list) {
            // 字段:判断是否有别名
            List<String> objKeyList = feildParams.getFieldList().stream().map(item-> {
                String sqlSelectCustomAnn = (String) item.getAnnotation().get("SQLSelectCustom");
                if(!Objects.isNull(sqlSelectCustomAnn) && sqlSelectCustomAnn.contains(" as ")){
                    String[] prefixSplitList = sqlSelectCustomAnn.split(" as ");
                    return AKSQLBaseTranslate.underlineToCamel(prefixSplitList[1]);
                }
                return AKSQLBaseTranslate.underlineToCamel(item.getName());
            }).collect(Collectors.toList());
            // 找出不存在字段
            objKeyList.removeAll(record.keySet());
            // 添加空字符串
            for (String nullKey : objKeyList) {
                record.put(nullKey, null);
            }

            // 特殊处理Long字段, 防止长度超出前端number类型限制
            for (Object key : record.keySet()) {
                String keyStr = key.toString();
                Object value = record.get(keyStr);
                if(value instanceof Long) {
                    record.put(keyStr, value.toString());
                }
            }

            // sql特定时间格式转换为时间戳
            translateTime(feildParams, record);
        }

        // 添加关联表
        addCollect(feildParams, list);
        return list;
    }


    private void fillEmptyFields(List<Map> recordList, AKFeildPageParams feildParams){
        for (Map record : recordList) {
            // 字段:判断是否有别名
            List<String> objKeyList = feildParams.getFieldList().stream().map(item-> {
                String sqlSelectCustomAnn = (String) item.getAnnotation().get("SQLSelectCustom");
                // 判断别名特征as
                if(!Objects.isNull(sqlSelectCustomAnn)
                        && sqlSelectCustomAnn.contains(" as ")){
                    String[] prefixSplitList = sqlSelectCustomAnn.split(" as ");
                    // 判断是否下划线命名
                    if(prefixSplitList[1].contains("_")){
                        return AKSQLBaseTranslate.underlineToCamel(prefixSplitList[1]);
                    }
                }
                return AKSQLBaseTranslate.underlineToCamel(item.getName());
            }).collect(Collectors.toList());
            // 找出不存在字段
            objKeyList.removeAll(record.keySet());
            // 添加
            for (String nullKey : objKeyList) {
                record.put(nullKey, null);
            }
        }
    }

}
