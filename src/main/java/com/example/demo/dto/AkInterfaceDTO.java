package com.example.demo.dto;

import com.example.demo.ak.akSQL.annotation.*;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@SQLAssociativeTable({"ak_interface ai","ak_tree_menu atm"})
@SQLEnd("ORDER BY ai.created_time DESC")
@SQLTableNick("ai")
public class AkInterfaceDTO {

    @SQLWhereEqual
    private String id;
    private String url;
    @SQLTableNick("atm")
    @SQLSelectCustom("name as menuName")
    private String menuName;
    private String tables;
    @SQLWhereEqual
    private String menuId;
    private String mark;
    private String deleted;
    private LocalDateTime createdTime;

}
