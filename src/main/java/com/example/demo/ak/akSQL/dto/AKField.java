package com.example.demo.ak.akSQL.dto;

import com.example.demo.ak.akSQL.annotation.SQLSelectExtend;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Data
public class AKField {

    Map<String, Object> annotation = new HashMap();
    String typeName = "";
    String name = "";

    public Boolean isAnnotationPresent(Class annotationClass){
        return !Objects.isNull(annotation.get(annotationClass.getSimpleName()));
    };

    public Object getAnnotation(Class annotationClass){
        return annotation.get(annotationClass.getSimpleName());
    }
    public Object setAnnotation(Class annotationClass, Object value){
        return annotation.put(annotationClass.getSimpleName(), value);
    }

}
