package com.home.mall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 具体的购物车项
 * @author: lyq
 * @createDate: 8/2/2023
 * @version: 1.0
 */
public class CartItemInfoVo {
    private Long skuId;//商品id
    private Boolean check=true; //是否被选中
    private String title;//商品标题
    private String image;//图片地址
    private List<String> skuAttr;//属性组合信息,哪套配置
    private BigDecimal price;//价格
    private Integer count;//数量
    private BigDecimal totalPrice;//总价

    public CartItemInfoVo(Long skuId, Boolean check, String title, String image, List<String> skuAttr, BigDecimal price, Integer count, BigDecimal totalPrice) {
        this.skuId = skuId;
        this.check = check;
        this.title = title;
        this.image = image;
        this.skuAttr = skuAttr;
        this.price = price;
        this.count = count;
        this.totalPrice = totalPrice;
    }

    public CartItemInfoVo() {
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalPrice() {
        totalPrice=price.multiply(new BigDecimal(""+count));
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
