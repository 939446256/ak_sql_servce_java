package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义查询条件,必须配合SQLWhereCustom查询
 *
 * 例子: 自定义查询条件
 * @SQLWhereCustom("(#{expirationByForever} and #{notEqualOrderCount})")
 * public class DefTenantMergeForeverListVO {
 *
 *     @SQLWhereCustomParams("expirationByForever")
 *     @SQLSelectCustom("expiration_time as expirationByForever")
 *     private LocalDateTime expirationByForever;
 *
 *     @SQLWhereNotEqual
 *     @SQLWhereCustomParams("notEqualOrderCount")
 *     @SQLSelectCustom("order_count as notEqualOrderCount")
 *     private Integer notEqualOrderCount;
 * }
 *
 * @author AK
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLWhereCustomParams {

    String value() default "";


}
