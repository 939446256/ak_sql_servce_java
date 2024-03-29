package com.example.demo.ak.akSQL.core;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import com.example.demo.ak.akSQL.annotation.SQLEnd;
import com.example.demo.ak.akSQL.annotation.SQLSelectCustom;
import com.example.demo.ak.akSQL.annotation.SQLTableNick;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLBaseTranslate {

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    // 获取前缀
    static Map<String, String> getPrefix(Object obj) {
        return getPrefix(obj.getClass());
    }

    // 获取前缀
    static Map<String, String> getCustomSQLMap(Class c) {
        Map<String, String> customSQLMap = new HashMap<>();

        for (Field item : c.getDeclaredFields()) {
            // 类型为数组
            if (item.getType().getName().equals("java.util.List")) {
                try {
                    Class arrayClass = ((Class) ((ParameterizedTypeImpl) item.getGenericType()).getActualTypeArguments()[0]);
                    Map<String, String> mergePreFixMap = getCustomSQLMap(arrayClass);
                    customSQLMap.putAll(mergePreFixMap);
                } catch (Exception err) {
                    continue;
                }
            }
            // 有独立注解
            else if (item.isAnnotationPresent(SQLSelectCustom.class)) {
                SQLSelectCustom SQLTableNick = item.getAnnotation(SQLSelectCustom.class);
                customSQLMap.put(item.toString(), SQLTableNick.value());
            }
        }
        return customSQLMap;
    }

    // 获取前缀
    static Map<String, String> getPrefix(Class c) {
        Map<String, String> preFixMap = new HashMap<>();
        String tableAnnotation = "";
        if (c.isAnnotationPresent(SQLTableNick.class)) {
            SQLTableNick globalSQLTableNick = (SQLTableNick) c.getAnnotation(SQLTableNick.class);
            tableAnnotation = globalSQLTableNick.value();
        }

        for (Field item : c.getDeclaredFields()) {
            // 类型为数组
            if (item.getType().getName().equals("java.util.List")) {
                try {
                    Class arrayClass = ((Class) ((ParameterizedTypeImpl) item.getGenericType()).getActualTypeArguments()[0]);
                    Map<String, String> mergePreFixMap = getPrefix(arrayClass);
                    preFixMap.putAll(mergePreFixMap);
                } catch (Exception err) {
                    continue;
                }
            }
            // 有独立注解
            else if (item.isAnnotationPresent(SQLTableNick.class)) {
                SQLTableNick SQLTableNick = item.getAnnotation(SQLTableNick.class);
                preFixMap.put(item.toString(), SQLTableNick.value());
            }
            // 有共有注解
            else if (!StringUtils.isEmpty(tableAnnotation)) {
                preFixMap.put(item.toString(), tableAnnotation);
            }
        }
        return preFixMap;
    }


    /**
     * 生成: 结尾附加SQL
     *
     * @param c 含有注解SQLEnd的类
     * @return SQL语句
     */
    public static String addToTableSql(Class c) {
        SQLEnd sqlEnd = (SQLEnd) c.getAnnotation(SQLEnd.class);
        if (ObjectUtils.isEmpty(sqlEnd)) {
            return "";
        }
        return " " + sqlEnd.value();
    }


    // ===========================  封装方法 ===============================
    // 驼峰转下划线
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    // 下划线转驼峰
    static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        char UNDERLINE = '_';
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = Character.toLowerCase(param.charAt(i));
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
