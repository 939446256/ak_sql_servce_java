package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义查询条件,可配合SQLWhereCustomParams查询
 * 例子1:
 * @SQLWhereCustom("table1.deleted = false")
 * public class DefTenantVO {
 * }
 *
 * 例子2:
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
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLWhereCustom {

    String value() default "";

}
