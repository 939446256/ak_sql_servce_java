package com.example.demo.dto;

import com.example.demo.ak.akSQL.annotation.SQLAssociativeTable;
import com.example.demo.ak.akSQL.annotation.SQLEnd;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@SQLAssociativeTable("ak_tree_menu")
@SQLEnd("ORDER BY sort asc")
public class AkMenu {

    private String id;
    private String name;
    private String parentId;
    private Integer sort;
    private String deleted;
    private LocalDateTime createdTime;

}
