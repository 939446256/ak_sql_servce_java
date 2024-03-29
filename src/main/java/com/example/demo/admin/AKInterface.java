package com.example.demo.admin;

import com.example.demo.ak.akSQL.annotation.SQLAssociativeTable;
import com.example.demo.ak.akSQL.annotation.SQLWhereEqual;
import com.example.demo.ak.akSQL.dto.AKField;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@SQLAssociativeTable("ak_interface ai")
public class AKInterface {

    @SQLWhereEqual
    private String id;
    @SQLWhereEqual
    private String url;
    private String tables;
    private String mark;
    private String menuId;
    private String tableDetail;
    private String classAnntations;
    private String testParams;
    private Boolean deleted;
}
