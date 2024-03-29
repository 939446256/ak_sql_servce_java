package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 传值为空值时,也会生成对应查询条件 (默认传空不处理)
 *
 * @author AK
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLWhereAllowNull {
    String value() default "";

}
