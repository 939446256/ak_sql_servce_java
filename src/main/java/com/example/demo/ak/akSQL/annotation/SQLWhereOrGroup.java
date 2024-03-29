package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 或 查询组合
 * 例子:
 * public class ModuleVO {
 *  @ApiModelProperty(value = "名称/编号搜索")
 *  @SQLIgnore
 *  private String searchValue;
 * 
 *  @ApiModelProperty(value = "编号/条码")
 *  @NotBlank(message = "请输入条形码")
 *  @SQLWhereOrGroup("searchValue")
 *  @SQLSelectCustom("name as searchName")
 *  private String searchName ;
 *
 *  @ApiModelProperty(value = "编号/条码")
 *  @NotBlank(message = "请输入条形码")
 *  @SQLWhereOrGroup("searchValue")
 *  @SQLSelectCustom("code as searchCode")
 *  private String searchCode ;
 * } 
 * 
 *  
 *

 * @author AK
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLWhereOrGroup {

    String value() default "";

//    String link() default "";

}
