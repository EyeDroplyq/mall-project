package com.home.mall.search.vo;

import com.home.common.to.es.SkuEsModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: es中检索完毕后发送给前端的响应实体vo
 * @author: lyq
 * @createDate: 29/1/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {

    private List<SkuEsModel> products;//所有的商品的sku信息

    private Integer pageNum;//当前页码
    private List<Integer> pageNavs;
    private Long total;//总数
    private Integer totalPages;//总页码
    private List<CatalogVo> catalogs; //所有的分类信息
    private List<BrandVo> brands;//所有的品牌数据
    private List<AttrVo> attrs;//所涉及的所有属性

    private List<NavVo> navs=new ArrayList<>(); //面包屑导航,如果没有进行面包屑选择就用这个默认值，防止出现空指针异常错误

    private List<Long> attrIds=new ArrayList<>();//用来给前端返回面包屑中的属性id

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;//跳转到哪里去
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BrandVo{
        private String brandName;
        private Long brandId;
        private String brandImg;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CatalogVo{
        private String catalogName;
        private Long catalogId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttrVo{
        private String attrName;
        private Long attrId;
        private List<String> attrValue;
    }
}
