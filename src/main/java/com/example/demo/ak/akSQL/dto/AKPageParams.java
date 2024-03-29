package com.example.demo.ak.akSQL.dto;


import com.example.demo.dto.AKIPage;
import lombok.Data;

import java.util.Map;

@Data
public class AKPageParams<T> {

//    @ApiModelProperty(
//            value = "查询参数",
//            required = true
//    )
    T model = null;
//    @ApiModelProperty(
//            value = "页面大小",
//            example = "10"
//    )
    private long size = 10L;
//    @ApiModelProperty(
//            value = "当前页",
//            example = "1"
//    )
    private long current = 1L;
//    @ApiModelProperty(
//            value = "排序,默认createdTime",
//            allowableValues = "id,createdTime,updatedTime",
//            example = "id"
//    )
    private String sort = "id";
//    @ApiModelProperty(
//            value = "排序规则, 默认descending",
//            allowableValues = "descending,ascending,asc,desc",
//            example = "descending"
//    )
    private String order = "descending";

    private long offset;

    public AKIPage<Map> buildPage(){
        AKIPage<Map> iPage = new AKIPage();
        iPage.setCurrent(this.current);
        iPage.setSize(this.size);
        return iPage;
    }

}
