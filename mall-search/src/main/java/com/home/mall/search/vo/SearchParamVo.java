package com.home.mall.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 前端用来进行检索的实体类封装的VO
 * @author: lyq
 * @createDate: 29/1/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchParamVo {

    private String keyword; //全文检索的字段

    private List<Long> brandId;  //品牌id

    private Long catagory3Id;  //三级分类的id

    private String sort;    //排序的条件

    private Integer hasStock; //是否有库存

    private String skuPrice; //价格区间

    private List<String> attrs;

    private Integer pageNum=1;//页码

    private String queryString;//前端请求的整个查询url

}
