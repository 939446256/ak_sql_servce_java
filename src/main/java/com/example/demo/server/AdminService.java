package com.example.demo.server;

import com.example.demo.ak.akSQL.dto.QuerySqlDTO;
import com.example.demo.dto.AKFeildPageParams;
import com.example.demo.dto.AKFeildParams;


public interface AdminService {
    // 转换AKSQL对象
    QuerySqlDTO simulateRequestParams(AKFeildParams dto);


    // 转换AKSQL对象
    QuerySqlDTO simulateRequestDeleteParams(AKFeildParams dto);

    // 分页: 转换AKSQL对象
    QuerySqlDTO simulateRequestPageParams(AKFeildPageParams dto) throws Exception;
}
