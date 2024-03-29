package com.example.demo.dto;

import com.example.demo.ak.akSQL.dto.AKField;
import com.example.demo.ak.akSQL.dto.AKPageParams;
import com.example.demo.config.AKFeildParamsInterface;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AKFeildPageParams implements AKFeildParamsInterface {

    private Map<String, Object> annotation;

    private List<String> associativeTable;

    private List<AKField> fieldList;

//    private PageParams<Map> requestParams;

    private AKPageParams<Map> requestParams;
}
