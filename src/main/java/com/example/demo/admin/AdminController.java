package com.example.demo.admin;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.example.demo.ak.akSQL.AKSQLMapper;
import com.example.demo.ak.akSQL.dto.*;
import com.example.demo.ak.akSQL.enums.SelectSQLExtendEnum;
import com.example.demo.config.AKTool;
import com.example.demo.dto.AKFeildParams;
import com.example.demo.dto.AkInterfaceDTO;
import com.example.demo.dto.AkMenu;
import com.example.demo.server.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AKSQLMapper sqlMapper;

    @Autowired
    private AdminService adminService;
    @Autowired
    private AKTool tool;

    @Value("${nodejs.url}")
    private String nodejsUrl;

    // 根据目录查找接口
    @GetMapping("getInterface/{menuId}")
    private List<AkInterfaceDTO> getInterface(@PathVariable String menuId) {
        AkInterfaceDTO interfaceDTO = new AkInterfaceDTO();
        // 根目录返回全部
        if(menuId.equals("1")){
            return sqlMapper.queryForList(AkInterfaceDTO.class);
        }
        interfaceDTO.setMenuId(menuId);
        return sqlMapper.queryForList(interfaceDTO);
    }



    // 目录结构
    @GetMapping("getMenu")
    private List<AkMenu> getMenu() {
        return sqlMapper.queryForList(AkMenu.class);
    }



    // 目录结构
    @PostMapping("updateInterfaceMenu")
    private Boolean updateInterfaceMenu(@RequestBody AkInterfaceDTO dto) {
        return sqlMapper.updateTable(dto);
    }


    // 目录结构
    @PostMapping("addMenu")
    private boolean addMenu(@RequestBody AkMenu dto) {
        return sqlMapper.insertAndUpdateTable(dto);
    }

    // 表关联关系
    @PostMapping("associativeTable/insertAndUpdate")
    private Boolean saveAssociativeTable(@RequestBody AssociativeTableDTO params) {
        return sqlMapper.insertAndUpdateTable(params);
    }



    // 表关联关系
    @PostMapping("associativeTable/batchDelete")
    private Boolean saveAssociativeTable(@RequestBody List<Long> ids) {
        return sqlMapper.batchDelete(ids, AssociativeTableDTO.class);
    }

    // 表关联关系
    @GetMapping("getAssociativeTable")
    private List getAssociativeTable() {
        List<AssociativeTableDTO> associativeTableDTOS = sqlMapper.queryForList(AssociativeTableDTO.class);
        for (AssociativeTableDTO associativeTableDTO : associativeTableDTOS) {
            associativeTableDTO.setTableFirstName(sqlMapper.getTableCommit(associativeTableDTO.getTableFirst()));
            associativeTableDTO.setTableSecondName(sqlMapper.getTableCommit(associativeTableDTO.getTableSecond()));
        }
        return associativeTableDTOS;
    }


    // 获取接口列表
    @GetMapping("queryIO")
    private List<AkInterfaceDTO> queryIO() {
        return sqlMapper.queryForList(AkInterfaceDTO.class);
    }


    // 1.获取所有表名
    @GetMapping("getAllTables")
    private List<String> getAllTables() {
        return sqlMapper.showTable();
    }
    // 1.1获取表注解
    @GetMapping("getTableCommit/{tableName}")
    private Map getTableCommit(@PathVariable("tableName") String tableName) {
        Map r = new HashMap();
        r.put("comment", sqlMapper.getTableCommit(tableName));
        return r;
    }
    // 2.获取所有表里的字段
    @PostMapping("getAllFieldsForTables")
    private List<Map> getAllFieldsForTables(@RequestBody Map dto) {
        return sqlMapper.getAllFieldsForTables(dto.get("table").toString());
    }


    /**
     * 模拟请求
     * 例子 : {"annotation":{"SQLAssociativeTable":["c_cases cc"]},"fieldList":[{"name":"id","type":"String","annotation":{"SQLWhereEqual":""}},{"name":"skilledLabel","type":"String"}],"requestParams":{"id":"00373acbcafe11e9965900163e04ea22"}}
     */
    @PostMapping("simulateRequest")
    private Object simulateRequest(@RequestBody Map params) throws Exception {
        // 通过nodeJs转换请求参数
        Object obj = request("POST", nodejsUrl + "/translateRequestParams", "{\"data\":" + params.get("showTableDetail") + "}");
        AKFeildParams dto = BeanUtil.copyProperties(obj, AKFeildParams.class);
        dto.setRequestParams(BeanUtil.copyProperties(params.get("requestParams"), Map.class));
        return sqlMapper.queryForListAK(dto);
    }

    @PostMapping("save")
    private String save(@RequestBody AKInterface dto) {
        sqlMapper.insertAndUpdateTable(dto);
        return dto.getId();
    }


    @PostMapping("copy")
    private Boolean copy(@RequestBody AKInterface dto) {
        AKInterface akInterface = sqlMapper.getDetail(dto);
        return sqlMapper.insertTable(akInterface);
    }

    @PostMapping("delete")
    private Boolean delete(@RequestBody AKInterface dto) {
        dto.setDeleted(Boolean.TRUE);
        return sqlMapper.updateTable(dto);
    }

    @GetMapping("detail/{id}")
    private AKInterface detail(@PathVariable String id) {
        AKInterface dto = new AKInterface();
        dto.setId(id);
        return sqlMapper.getDetail(dto);
    }

    @PostMapping("show")
    private String show(@RequestBody Map params) throws IOException {
        // 通过nodeJs转换请求参数
        Object obj = request("POST", nodejsUrl + "/translateRequestParams", "{\"data\":" + params.get("showTableDetail") + "}");
        AKFeildParams dto = BeanUtil.copyProperties(obj, AKFeildParams.class);
        dto.setRequestParams(BeanUtil.copyProperties(params.get("requestParams"), Map.class));

        AKClass akClass = new AKClass();
        AKObject akObject = new AKObject();
        List<AKField> akFieldList = new ArrayList<>();
        for (AKField requestParam : dto.getFieldList()) {
            akFieldList.add(requestParam);
        }
        akClass.setField(akFieldList);

        // 赋值: 链表
        for (String key : dto.getAnnotation().keySet()) {
            Object item = dto.getAnnotation().get(key);
            if(item instanceof String) {
                akClass.getAnnotationMap().put(key, item);
            } else if(dto.getAnnotation().get(key) instanceof List) {
                akClass.getAnnotationMap().put(key, item);
            }
        }
        akObject.setMap(dto.getRequestParams());
        // 转SQL
        QuerySqlDTO querySqlDTO = new QuerySqlDTO(akClass, akObject, sqlMapper);
        String sql =  " SELECT  "+querySqlDTO.getSelectSql()+" FROM " + querySqlDTO.getTableSql();
        if(!StringUtils.isEmpty(querySqlDTO.getWhereSql())){
            sql += " where " + querySqlDTO.getWhereSql();
        }
        sql += querySqlDTO.getAddSql();
        return sql;
    }

    @GetMapping("getSelectSQLExtends")
    private List getSelectSQLExtends(){
        List<Map> result = new ArrayList<>();
        for (SelectSQLExtendEnum value : SelectSQLExtendEnum.values()) {
            Map map = new HashMap<>();
            map.put("name",value.toString());
            map.put("annotate",value.getAnnotate());
            result.add(map);
        }
        return result;
    }



    private Object request(String requestMethod, String path,String postContent) throws IOException {
//        R clSendResult = R.success();
        URL url = new URL(path);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod(requestMethod);
        httpUrlConnection.setConnectTimeout(10000);
        httpUrlConnection.setReadTimeout(10000);
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setRequestProperty("Charset", "UTF-8");
        httpUrlConnection.setRequestProperty("Content-Type", "application/json");
        httpUrlConnection.connect();
        if(requestMethod.equals("POST")){
            OutputStream os = httpUrlConnection.getOutputStream();
            os.write(postContent.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
        StringBuilder sb = new StringBuilder();
        int httpRspCode = httpUrlConnection.getResponseCode();
        if (httpRspCode == HttpURLConnection.HTTP_OK || httpRspCode == HttpURLConnection.HTTP_CREATED) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpUrlConnection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            Object parse = JSON.parse(sb.toString());
            return parse;
        }
        return null;
    }
}
