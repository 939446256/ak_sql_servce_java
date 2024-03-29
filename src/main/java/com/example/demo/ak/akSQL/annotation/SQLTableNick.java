package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定属性查询的表别名
 * 例子1: 指定对象属性查询表别名
 * @SQLAssociativeTable({"c_storage_goods sg", "c_goods g"})
 * public class CheckGoodsLimitCountDTO {
 *     @ApiModelProperty(value = "仓库ID")
 *     @SQLTableNick("sg")
 *     @SQLSelectCustom("warehouse_id as warehouseId")
 *     private String warehouseId;
 * }
 *
 * 例子2: 指定该对象属性默认查询表别名
 * @SQLAssociativeTable({"c_storage_goods sg", "c_goods g"})
 * @SQLTableNick("g")
 * public class CheckGoodsLimitCountDTO {
 *     @ApiModelProperty(value = "商品ID")
 *     @SQLWhereEqual
 *     private String id;
 *
 *     @ApiModelProperty(value = "仓库ID")
 *     @SQLTableNick("sg")
 *     @SQLSelectCustom("warehouse_id as warehouseId")
 *     private String warehouseId;
 * }
 *
 *
 * @author AK
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLTableNick {

    String value() default "";


}
