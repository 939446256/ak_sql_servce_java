package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * 子查询,对象里镶嵌Object的查询
 * 例子:
 * // 外层对象
 * public class QueryEmbedReportForListDTO {
 *     @ApiModelProperty(value = "嵌套查询")
 *     @SQLEmbedTable
 *     private QueryReportForListDTO mergeTable;
 *
 *     @ApiModelProperty(value = "库存量")
 *     @SQLWhereNotEqual
 *     private String storageCount ;
 * }
 *
 * // 内层对象
 * public class QueryReportForListDTO {
 *
 *     @ApiModelProperty(value = "ID")
 *     @SQLWhereEqual
 *     private String id;
 *
 *     @ApiModelProperty(value = "商品分类ID")
 *     @SQLWhereEqual
 *     private String classesId;
 * }
 *
 * @author AK
 */
// 镶嵌Object查询
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLEmbedTable {
    String value() default "";
}
