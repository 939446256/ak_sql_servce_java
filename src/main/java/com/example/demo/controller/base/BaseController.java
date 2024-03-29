package com.example.demo.controller.base;

import com.example.demo.ak.akSQL.AKSQLMapper;
import com.example.demo.ak.akSQL.dto.AKPageParams;
import com.example.demo.ak.akSQL.dto.QuerySqlDTO;
import com.example.demo.config.AKTool;
import com.example.demo.dto.AKFeildPageParams;
import com.example.demo.dto.AKFeildParams;
import com.example.demo.dto.AKIPage;
import com.example.demo.server.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/base")
@CrossOrigin(origins = "*")
public class BaseController {


    @Autowired
    private AKSQLMapper sqlMapper;
    @Autowired
    private AdminService adminService;
    @Autowired
    private AKTool tool;

    // POST:新增
    @PostMapping("{tableName}/insert")
    private String insert(@PathVariable String tableName, @RequestBody Map dto) {
        return sqlMapper.insertTableAK(tableName, dto);
    }

    // POST:修改
    @PostMapping("{tableName}/update")
    private String UpdateTable(@PathVariable String tableName, @RequestBody Map dto) {
        return sqlMapper.updateTableAK(tableName, dto);
    }


    // POST:新增或修改
    @PostMapping("{tableName}/insertAndUpdate")
    private String insertAndUpdateTableAK(@PathVariable String tableName, @RequestBody Map dto) {
        return sqlMapper.insertAndUpdateTableAK(tableName, dto);
    }


    // POST:根据ID数组批量删除
    @PostMapping("{tableName}/batchDelete")
    private Boolean batchDelete(@PathVariable String tableName, @RequestBody List<Long> ids) {
        return sqlMapper.batchDeleteAK(tableName, ids);
    }

    // POST:真删除
    @PostMapping("{model}/{io}/deleteFromTable")
    private Boolean deleteFromTable(@PathVariable String model,@PathVariable String io, @RequestBody Map body) throws IOException {
        // 拼接 接口和参数
        AKFeildParams feildParams = tool.getIOParams(model + "/" + io, body);
        return sqlMapper.deleteFromTableAK(feildParams, adminService.simulateRequestDeleteParams(feildParams));
    }

    // GET:获取列表
    @GetMapping("{model}/{io}")
    private Object getModel(@PathVariable String model,@PathVariable String io) throws Exception {
        Map body = new HashMap();
        AKFeildParams feildParams = tool.getIOParams(model + "/" + io, body);
        return sqlMapper.queryForListAK(feildParams);
    }


    // GET:根据ID获取详情
    @GetMapping("{model}/{io}/{id}")
    private Object getDetail(@PathVariable String model,@PathVariable String io,@PathVariable String id) throws Exception {
        Map body = new HashMap();
        body.put("id", id);
        AKFeildParams feildParams = tool.getIOParams(model + "/" + io, body);
        return sqlMapper.getDetailAK(feildParams);
    }


    // POST:根据条件获取最近一条详情
    @PostMapping("{model}/{io}/detail")
    private Object requestDetail(@PathVariable String model,@PathVariable String io,@RequestBody Map body) throws Exception {
        AKFeildParams feildParams = tool.getIOParams(model + "/" + io, body);
        return sqlMapper.getDetailAK(feildParams);
    }

    // POST:根据条件判断是否有值
    @PostMapping("{model}/{io}/hasData")
    private Boolean hasData(@PathVariable String model,@PathVariable String io,@RequestBody Map body) throws Exception {
        AKFeildParams feildParams = tool.getIOParams(model + "/" + io, body);
        return sqlMapper.hasData(feildParams);
    }

    // POST:根据条件获取最近一条详情
    @PostMapping("{model}/{io}/count")
    private Object count(@PathVariable String model,@PathVariable String io,@RequestBody Map body) throws Exception {
        AKFeildParams feildParams = tool.getIOParams(model + "/" + io, body);
        return sqlMapper.getCountAK(feildParams);
    }



    // POST:根据条件获取列表
    @PostMapping("{model}/{io}/list")
    private Object postModel(@PathVariable String model,@PathVariable String io, @RequestBody Map body) throws Exception {
        // 拼接 接口和参数
        AKFeildParams feildParams = tool.getIOParams(model + "/" + io, body);
        return sqlMapper.queryForListAK(feildParams);
    }


    // POST:根据条件获取分页列表
    @PostMapping("{model}/{io}/page")
    private AKIPage<Map> postPage(@PathVariable String model, @PathVariable String io, @RequestBody AKPageParams<Map> body) throws Exception {
        AKFeildPageParams feildParams = tool.getIOPageParams(model + "/" + io, body);
        return sqlMapper.queryForPageListAK(feildParams);

    }



}
