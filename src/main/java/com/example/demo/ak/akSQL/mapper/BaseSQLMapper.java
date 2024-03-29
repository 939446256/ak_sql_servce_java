package com.example.demo.ak.akSQL.mapper;

import com.example.demo.ak.akSQL.dto.AKPageParams;
import com.example.demo.dto.AKIPage;
import com.example.demo.dto.AkInterfaceDTO;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.ak.akSQL.dto.InsertSqlDTO;
import com.example.demo.ak.akSQL.dto.QuerySqlDTO;
import com.example.demo.ak.akSQL.dto.UpdateSqlDTO;

import java.util.List;
import java.util.Map;

@Repository
public interface BaseSQLMapper  {

    Boolean insertTable(@Param("example") InsertSqlDTO example);

    Boolean updateTable(@Param("example") UpdateSqlDTO example);

//    IPage<Map<String, Object>> queryForPageList(IPage page, @Param("params") PageParams<QuerySqlDTO> wraps);
    int queryForPageCount(@Param("params") AKPageParams<QuerySqlDTO> wraps);

    AKIPage<Map<String, Object>> queryForPageList(AKIPage page, @Param("params") AKPageParams<QuerySqlDTO> wraps);

    List<Map> queryForPageList2(AKPageParams page, @Param("params") QuerySqlDTO wraps);


    List<Map<String, Object>> queryForList(@Param("querySqlDto") QuerySqlDTO querySqlDTO);

    List<Map<String, Object>> queryForCollectionList(@Param("querySqlDto") QuerySqlDTO querySqlDTO);

    Boolean batchUpdate(@Param("example") UpdateSqlDTO example, @Param("ids") List<Long> ids);

    Boolean batchSoftDelete(@Param("table") String table, @Param("ids") List<Long> ids);

    Boolean deleteFromTable(@Param("table") String table, @Param("whereSQL") String whereSQL);

    Integer getCount(@Param("querySqlDto") QuerySqlDTO querySqlDTO);

    Integer executeColumnWork(@Param("table") String table,
                              @Param("column") String column,
                              @Param("command") String command,
                              @Param("type") String type
                              );

    List<String> showTable();

    String getTableCommit(@Param("tableName") String tableName);

    List<Map> getAllFieldsForTables(@Param("table") String table);

}
