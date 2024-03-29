package com.example.demo.ak.akSQL.dto;

import com.example.demo.ak.akSQL.annotation.SQLAssociativeTable;
import com.example.demo.ak.akSQL.annotation.SQLEnd;
import com.example.demo.ak.akSQL.annotation.SQLIgnore;
import lombok.Data;

@Data
@SQLAssociativeTable("ak_associative_table aat")
@SQLEnd("ORDER BY created_time desc")
public class AssociativeTableDTO {

    private String id;

    private String tableFirst;
    @SQLIgnore
    private String tableFirstName;
    private String tableSecond;
    @SQLIgnore
    private String tableSecondName;
    private String associativeFieldFirst;
    private String associativeFieldSecond;
    private Boolean deleted;
}
