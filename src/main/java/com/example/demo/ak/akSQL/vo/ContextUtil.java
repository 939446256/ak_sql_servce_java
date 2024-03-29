package com.example.demo.ak.akSQL.vo;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ContextUtil {

    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal();

    private ContextUtil() {}

    public static void putAll(Map<String, String> map) {
        map.forEach(ContextUtil::set);
    }

    public static void set(String key, Object value) {
        if (!ObjectUtil.isEmpty(value) && !StrUtil.isBlankOrUndefined(value.toString())) {
            Map<String, String> map = getLocalMap();
            map.put(key, value.toString());
        }
    }

    public static <T> T get(String key, Class<T> type) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.get(key));
    }

    public static <T> T get(String key, Class<T> type, Object def) {
        Map<String, String> map = getLocalMap();
        String value;
        if (def == null) {
            value = (String)map.get(key);
        } else {
            value = (String)map.getOrDefault(key, String.valueOf(def));
        }

        return Convert.convert(type, StrUtil.isEmpty(value) ? def : value);
    }

    public static String get(String key) {
        Map<String, String> map = getLocalMap();
        return (String)map.getOrDefault(key, "");
    }

    public static Map<String, String> getLocalMap() {
        Map<String, String> map = (Map)THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap(10);
            THREAD_LOCAL.set(map);
        }

        return (Map)map;
    }


    public static Long getUserId() {
        return (Long)get("UserId", Long.class);
    }

    public static void setUserId(Object userId) {
        set("UserId", userId);
    }

    public static Long getEmployeeId() {
        return (Long)get("EmployeeId", Long.class);
    }

    public static void setEmployeeId(Object employeeId) {
        set("EmployeeId", employeeId);
    }

    public static boolean isEmptyTenantId() {
        return isEmptyLong("TenantId");
    }

    public static boolean isEmptyBasePoolNameHeader() {
        return isEmptyBasePool();
    }

    public static boolean isEmptyBasePool() {
        return isEmptyLong("wuyang");
    }

    public static boolean isEmptyUserId() {
        return isEmptyLong("UserId");
    }

    public static boolean isEmptyEmployeeId() {
        return isEmptyLong("EmployeeId");
    }

    public static boolean isEmptyApplicationId() {
        return isEmptyLong("ApplicationId");
    }

    public static Long getTenantId() {
        return (Long)get("TenantId", Long.class);
    }

    public static void setTenantId(Object tenantId) {
        set("TenantId", tenantId);
        setTenantBasePoolName(tenantId);
    }

    public static Long getBasePoolNameHeader() {
        return (Long)get("wuyang", Long.class);
    }

    public static String getTenantIdStr() {
        return String.valueOf(getTenantId() == null ? "" : getTenantId());
    }

    public static void setTenantBasePoolName(Object tenantId) {
        set("wuyang", tenantId);
    }




    public static Long getApplicationId() {
        return (Long)get("ApplicationId", Long.class);
    }

    public static void setApplicationId(Object applicationId) {
        set("ApplicationId", applicationId);
    }

    public static String getPath() {
        return (String)get("Path", String.class, "");
    }

    public static void setPath(Object path) {
        set("Path", path == null ? "" : path);
    }

    public static String getToken() {
        return (String)get("Token", String.class);
    }

    public static void setToken(String token) {
        set("Token", token == null ? "" : token);
    }

    public static Long getCurrentCompanyId() {
        return (Long)get("CurrentCompanyId", Long.class);
    }

    public static void setCurrentCompanyId(Object val) {
        set("CurrentCompanyId", val);
    }

    public static Long getCurrentTopCompanyId() {
        return (Long)get("CurrentTopCompanyId", Long.class);
    }

    public static void setCurrentTopCompanyId(Object val) {
        set("CurrentTopCompanyId", val);
    }

    public static Long getCurrentDeptId() {
        return (Long)get("CurrentDeptId", Long.class);
    }

    public static void setCurrentDeptId(Object val) {
        set("CurrentDeptId", val);
    }

    public static String getClientId() {
        return (String)get("ClientId", String.class);
    }

    public static void setClientId(String val) {
        set("ClientId", val);
    }

    private static boolean isEmptyLong(String key) {
        String val = (String)getLocalMap().get(key);
        return StrUtil.isEmpty(val) || "null".equals(val) || "0".equals(val);
    }

    private static boolean isEmptyStr(String key) {
        String val = (String)getLocalMap().get(key);
        return val == null || "null".equals(val);
    }

    public static String getLogTraceId() {
        return (String)get("trace", String.class);
    }

    public static void setLogTraceId(String val) {
        set("trace", val);
    }

    public static Boolean getBoot() {
        return (Boolean)get("boot", Boolean.class, false);
    }

    public static void setBoot(Boolean val) {
        set("boot", val);
    }

    public static String getGrayVersion() {
        return (String)get("gray_version", String.class);
    }

    public static void setGrayVersion(String val) {
        set("gray_version", val);
    }

    public static Boolean isProceed() {
        String proceed = (String)get("proceed", String.class);
        return StrUtil.isNotEmpty(proceed);
    }

    public static void setProceed() {
        set("proceed", "1");
    }

    public static Boolean isStop() {
        String proceed = (String)get("stop", String.class);
        return StrUtil.isNotEmpty(proceed);
    }

    public static void setStop() {
        set("stop", "1");
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
