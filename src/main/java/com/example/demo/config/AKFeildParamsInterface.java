package com.example.demo.config;

import com.example.demo.ak.akSQL.dto.AKField;
import lombok.Data;

import java.util.List;
import java.util.Map;

public interface AKFeildParamsInterface {

    Map<String, Object> annotation = null;

    List<String> associativeTable = null;

    List<AKField> fieldList = null;
    
    default Map<String, Object> getAnnotation(){
        return annotation;
    }

    default List<AKField> getFieldList(){
        return fieldList;
    }
    default List<String> getAssociativeTable(){
        return associativeTable;
    }


}
