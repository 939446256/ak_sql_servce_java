package com.example.demo.ak.akSQL.annotation;

import com.example.demo.ak.akSQL.enums.SelectSQLExtendEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 查询语句扩展,必须配合SelectSQLExtendEnum扩展
 * 例子1: 通过创建人ID 指查询 创建人名称
 *  @ApiModelProperty(value = "创建人账号")
 *  @SQLSelectExtend(SelectSQLExtendEnum.GET_USER_NAME)
 *  @SQLTableNick("user")
 *  @SQLSelectCustom("createdBy as createdByName")
 *  private String  createdByName ;
 *
 *  例子2: 多层嵌套(顺序从前到后,从内到外)
 *     @Excel(name = "出库数量", width = 30)
 *     @ApiModelProperty(value = "出库数量")
 *     @SQLSelectExtend({SelectSQLExtendEnum.GET_OUT_STORAGE_ORDER_COUNT,SelectSQLExtendEnum.DEFAULT_NUMBER_VALUE})
 *     @SQLSelectCustom("number as outStorageCount")
 *     private String outStorageCount ;
 *
 * @author AK
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLSelectExtend {
    SelectSQLExtendEnum[] value();
}
