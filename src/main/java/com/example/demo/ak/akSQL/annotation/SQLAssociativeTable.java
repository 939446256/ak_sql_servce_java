package com.example.demo.ak.akSQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

/**
 * 关联表标签
 * 例子: @SQLAssociativeTable({"c_order id","c_order_check_record order_id"})
 *
 * @author AK
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLAssociativeTable {
    // 数据表-关系表
    List<List<String>> relationList = Arrays.asList(
            Arrays.asList("ak_tree_menu", "id",
                    "ak_interface", "menu_id")
    );

    String[] value() default "";


}
