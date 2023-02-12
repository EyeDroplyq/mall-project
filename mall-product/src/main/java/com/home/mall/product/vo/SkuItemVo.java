package com.home.mall.product.vo;

import com.home.mall.product.entity.SkuImagesEntity;
import com.home.mall.product.entity.SkuInfoEntity;
import com.home.mall.product.entity.SpuInfoDescEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @description: 商品详情页的VO
 * @author: lyq
 * @createDate: 31/1/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuItemVo {

    //sku的基本信息
    private SkuInfoEntity info;

    //spu的介绍信息
    private SpuInfoDescEntity desp;

    private boolean hasStock=true;//默认有货

    //sku的图片信息
    private List<SkuImagesEntity> images;

    //spu对应的销售属性
    private List<SkuItemSaleAttrVo> saleAttr;

    //分组属性信息
    private List<SpuItemAttrGroupVo> groupAttrs;





    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class SkuItemSaleAttrVo{
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;

    }


    //spu的分组属性
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class SpuItemAttrGroupVo{
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }

    //spu分组中的基本属性
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class SpuBaseAttrVo{
    	private Long attrId;
        private String attrName;
        private String attrValue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class AttrValueWithSkuIdVo{
        private String attrValue;
        private String skuIds;
    }

}
