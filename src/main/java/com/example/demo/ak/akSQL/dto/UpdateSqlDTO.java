package com.example.demo.ak.akSQL.dto;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import org.apache.commons.beanutils.BeanMap;
import com.example.demo.ak.akSQL.annotation.SQLUpdateWhere;
import com.example.demo.ak.akSQL.core.SQLInsertOrUpdateTranslate;
import com.example.demo.ak.akSQL.core.SQLSelectTranslate;
import com.example.demo.ak.akSQL.core.SQLWhereTranslate;

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
public class UpdateSqlDTO {

    public UpdateSqlDTO(){
    }

    public UpdateSqlDTO(String tableName, Object dto){
        this.tableName = tableName;
        BeanMap dtoToMap = new BeanMap(dto);

        // 更新参数SQL
        Map sqlMap = new HashMap();
        // 条件参数SQL
        Map whereMap = new HashMap();


        Boolean hasCustomUpdateWhere = Boolean.FALSE;
        // 获取所有SQLUpdateWhere标签
        for (Field field : dto.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(SQLUpdateWhere.class)){
                whereMap.put(field.getName(), dtoToMap.get(field.getName()));
                hasCustomUpdateWhere = Boolean.TRUE;
            } else {
                sqlMap.put(field.getName(), dtoToMap.get(field.getName()));
            }
        }

        // 判断是否有自定义筛选条件字段
        if(hasCustomUpdateWhere){
            try {
                Object sqlObject = BeanUtil.copyProperties(sqlMap, dto.getClass());
                this.updateSQL = SQLInsertOrUpdateTranslate.mapToSql(sqlObject);
                Object whereObject = BeanUtil.copyProperties(whereMap, dto.getClass());
                this.whereSQL = SQLWhereTranslate.mapToQuerySql(whereObject, dto.getClass());
            } catch (Exception err){
                System.out.println(err);
            }
        } else {
            // 默认取ID作为筛选条件字段
            this.whereSQL = "id = " + dtoToMap.get("id");
            this.updateSQL = SQLInsertOrUpdateTranslate.mapToSql(dto);
        }
    }

    public UpdateSqlDTO(String tableName, String sql){
        this.tableName = tableName;
        this.updateSQL = sql;
    }

    private Long id;

    private String tableName;

    private String whereSQL;

    private String updateSQL;


}
