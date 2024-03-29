package com.example.demo.ak.akSQL.dto;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class AKObject {

    Map<String, Object> map = new HashMap<>();

}
