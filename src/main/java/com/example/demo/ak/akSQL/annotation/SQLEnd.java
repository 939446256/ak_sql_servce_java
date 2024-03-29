package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 在生成SQL查询语句后, 结尾添加
 * 例子: 添加分组
 * @SQLEnd("GROUP BY e.id")
 * public class OrderSalesmanListDTO {
 * }
 *
 * @author AK
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLEnd {
    String value();
}
