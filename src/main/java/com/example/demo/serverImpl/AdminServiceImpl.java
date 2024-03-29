package com.example.demo.serverImpl;

import com.example.demo.ak.akSQL.AKSQLMapper;
import com.example.demo.ak.akSQL.dto.AKClass;
import com.example.demo.ak.akSQL.dto.AKField;
import com.example.demo.ak.akSQL.dto.AKObject;
import com.example.demo.ak.akSQL.dto.QuerySqlDTO;
import com.example.demo.dto.AKFeildPageParams;
import com.example.demo.dto.AKFeildParams;
import com.example.demo.server.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AKSQLMapper sqlMapper;

    @Override
    public QuerySqlDTO simulateRequestParams(AKFeildParams dto) {
        AKClass akClass = new AKClass();
        AKObject akObject = new AKObject();


        List<AKField> akFieldList = new ArrayList<>();
        // 赋值: 获取标签
        for (AKField requestParam : dto.getFieldList()) {
            akFieldList.add(requestParam);
        }
        akClass.setField(akFieldList);

        // 赋值: 获取全局标签
        for (String key : dto.getAnnotation().keySet()) {
            Object item = dto.getAnnotation().get(key);
            if(item instanceof String){
                akClass.getAnnotationMap().put(key, item);
            } else if(dto.getAnnotation().get(key) instanceof List){
                akClass.getAnnotationMap().put(key, item);
            }
        }

        akObject.setMap(dto.getRequestParams());

        // 转SQL
        return new QuerySqlDTO(akClass, akObject, sqlMapper);
//        QuerySqlDTO querySqlDTO = new QuerySqlDTO(akClass, akObject, sqlMapper);
//        return sqlMapper.queryForListAK(querySqlDTO);
    }

    @Override
    public QuerySqlDTO simulateRequestDeleteParams(AKFeildParams dto) {
        AKClass akClass = new AKClass();
        AKObject akObject = new AKObject();


        List<AKField> akFieldList = new ArrayList<>();
        // 赋值: 获取标签
        for (AKField requestParam : dto.getFieldList()) {
            // 清除所有SQLTableNick标签
            requestParam.getAnnotation().remove("SQLTableNick");
            akFieldList.add(requestParam);
        }
        akClass.setField(akFieldList);

        // 赋值: 获取全局标签
        for (String key : dto.getAnnotation().keySet()) {
            Object item = dto.getAnnotation().get(key);
            if(item instanceof String){
                akClass.getAnnotationMap().put(key, item);
            } else if(dto.getAnnotation().get(key) instanceof List){
                akClass.getAnnotationMap().put(key, item);
            }
        }

        akObject.setMap(dto.getRequestParams());

        // 转SQL
        return new QuerySqlDTO(akClass, akObject, sqlMapper);
//        QuerySqlDTO querySqlDTO = new QuerySqlDTO(akClass, akObject, sqlMapper);
//        return sqlMapper.queryForListAK(querySqlDTO);
    }

    @Override
    public QuerySqlDTO simulateRequestPageParams(AKFeildPageParams dto) throws Exception {
        if(Objects.isNull(dto.getRequestParams().getModel())){
            throw new Exception("model参数不能为空");
        }

        AKClass akClass = new AKClass();
        AKObject akObject = new AKObject();


        List<AKField> akFieldList = new ArrayList<>();
        // 赋值: 获取标签
        for (AKField requestParam : dto.getFieldList()) {
            akFieldList.add(requestParam);
        }
        akClass.setField(akFieldList);

        // 赋值: 获取全局标签
        for (String key : dto.getAnnotation().keySet()) {
            Object item = dto.getAnnotation().get(key);
            if(item instanceof String){
                akClass.getAnnotationMap().put(key, item);
            } else if(dto.getAnnotation().get(key) instanceof List){
                akClass.getAnnotationMap().put(key, item);
            }
        }

        akObject.setMap(dto.getRequestParams().getModel());

        // 转SQL
        return new QuerySqlDTO(akClass, akObject, sqlMapper);
//        QuerySqlDTO querySqlDTO = new QuerySqlDTO(akClass, akObject, sqlMapper);
//        return sqlMapper.queryForListAK(querySqlDTO);
    }

}
