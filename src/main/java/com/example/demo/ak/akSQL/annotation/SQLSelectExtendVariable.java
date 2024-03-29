package com.example.demo.ak.akSQL.annotation;

import com.example.demo.ak.akSQL.enums.SelectSQLExtendEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 扩展查询变量,必须配合SQLSelectExtend使用
 * 例子: 替换SQLSelectExtend扩展中的{warehouseId}变量
 * public class test {
 *     @ApiModelProperty(value = "仓库ID")
 *     @SQLSelectExtendVariable("AND warehouse_id = {warehouseId}")
 *     private String warehouseId;
 *
 *     @Excel(name = "出库数量", width = 30)
 *     @ApiModelProperty(value = "出库数量")
 *     @SQLSelectExtend({SelectSQLExtendEnum.GET_OUT_STORAGE_ORDER_COUNT})
 *     @SQLSelectCustom("number as outStorageCount")
 *     private String outStorageCount ;
 * }
 *
 * @author AK
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLSelectExtendVariable {
    String value() default "";
}
