package com.home.common.to.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 用来保存数据到es的to
 * @author: lyq
 * @createDate: 23/1/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuEsModel {

    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long brandId;

    private Long catalogId;

    private String brandName;

    private String brandImg;

    private String catalogName;

    private List<Attr> attrs;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attr{

        private Long attrId;

        private String attrName;

        private String attrValue;
    }
}
