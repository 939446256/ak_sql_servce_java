package com.example.demo.ak.akSQL.dto;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Data
public class AKClass {

    Map<String, Object> annotationMap = new HashMap();
    String typeName = "";
    String name = "";

    List<AKField> field = new ArrayList<>();

    Map<String, Object> dtoMap = new HashMap<>();

    public Boolean isAnnotationPresent(Class annotationClass){
        return !ObjectUtils.isEmpty(annotationMap.get(annotationClass.getSimpleName()));
    };

    public Object getAnnotation(Class annotationClass){
        return annotationMap.get(annotationClass.getSimpleName());
    }

    public List<AKField> getDeclaredFields(){
        return field;
    }

    public AKObject getConstructor(){
        return new AKObject();
    }
}
