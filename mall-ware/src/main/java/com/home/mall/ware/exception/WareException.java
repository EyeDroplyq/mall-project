package com.home.mall.ware.exception;

/**
 * @description:
 * @author: lyq
 * @createDate: 24/2/2023
 * @version: 1.0
 */
public class WareException extends RuntimeException{
    private Long skuId;

    public WareException(Long skuId) {
        super("商品"+skuId+"没有库存了");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
