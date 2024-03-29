package com.example.demo.ak.akSQL.enums;

import java.util.stream.Stream;

public enum OrderEnum {
    ascending("asc"),
    asc("asc"),
    descending("desc"),
    desc("desc"),
    ;

    private OrderEnum(String str){
        code = str;
    }

    public static OrderEnum get(String val) {
        return match(val, null);
    }

    public static OrderEnum match(String val, OrderEnum def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }


    private String code;

    public String getCode() {
        return this.code;
    }
}
