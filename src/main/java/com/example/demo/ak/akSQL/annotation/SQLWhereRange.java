package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 时间范围: 开始时间和结束时间属性命名格式(xxxxStart / xxxxEnd)
 * 例子: 按照固定命名
 *
 * @ApiModelProperty(value = "创建时间-范围开始")
 * @SQLWhereTimeRange
 * private LocalDateTime createdTimeStart ;
 * @ApiModelProperty(value = "创建时间-范围结束")
 * @SQLWhereTimeRange
 * private LocalDateTime createdTimeEnd ;
 *
 * @author AK
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLWhereRange {

    String value() default "";


}
