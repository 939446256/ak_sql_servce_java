package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义Select查询 (可配合SQLTableNick/SQLExtend使用)
 * 例子: created_by 起别名 createdById
 * @SQLWhereEqual
 * @SQLSelectCustom("created_by as createdById")
 * private String createdById;
 *
 * @author AK
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLSelectCustom {

    String value() default "";

}
