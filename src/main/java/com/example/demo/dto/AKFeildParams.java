package com.example.demo.dto;

import com.example.demo.ak.akSQL.dto.AKField;
import com.example.demo.config.AKFeildParamsInterface;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AKFeildParams implements AKFeildParamsInterface {

    private Map<String, Object> annotation;

    private List<String> associativeTable;

    private List<AKField> fieldList;

    private Map requestParams;

    // 防SQLSelectCollect注入重复
    private Boolean hasCollect = Boolean.FALSE;
}
