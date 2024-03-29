package com.example.demo.ak.akSQL.dto;

import lombok.Data;
import com.example.demo.ak.akSQL.core.SQLInsertOrUpdateTranslate;

/**
 * <p>
 * 实体类
 * </p>
 *
 * @author AK
 * @since 2022-06-16
 */
@Data
public class InsertSqlDTO {

    public InsertSqlDTO(){
    }

    public InsertSqlDTO(String tableName, Object dto){
        this.tableName = tableName;
        this.sql = SQLInsertOrUpdateTranslate.mapToSql(dto);
    }

    public InsertSqlDTO(String tableName, String sql){
        this.tableName = tableName;
        this.sql = sql;
    }

    private Long id;

    private String tableName;

    private String sql;
}
