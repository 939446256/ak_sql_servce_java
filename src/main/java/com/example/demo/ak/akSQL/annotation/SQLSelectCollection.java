package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应对表关系是一对多映射 (生成mybatise的collection标签)
 * 例子:
 * // 图片
 * @SQLSelectCollection
 * private List<OrderAdjunctDTO> images;
 *
 * @author AK
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLSelectCollection {

    String value() default "";

}
