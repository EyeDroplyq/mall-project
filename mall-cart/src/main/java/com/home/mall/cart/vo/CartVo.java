package com.home.mall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 整个购物车
 * @author: lyq
 * @createDate: 8/2/2023
 * @version: 1.0
 */
public class CartVo {
    private List<CartItemInfoVo> items;//每个用户的购物车
    private Integer countNum;//商品数量
    private Integer countType;//商品类型数量
    private BigDecimal totalAmount;//商品总价
    private BigDecimal reduce=new BigDecimal("0.00");//减免价格

    public CartVo(List<CartItemInfoVo> items, Integer countNum, Integer countType, BigDecimal totalAmount, BigDecimal reduce) {
        this.items = items;
        this.countNum = countNum;
        this.countType = countType;
        this.totalAmount = totalAmount;
        this.reduce = reduce;
    }

    public CartVo() {
    }

    public List<CartItemInfoVo> getItems() {
        return items;
    }

    public void setItems(List<CartItemInfoVo> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        countNum=0;
       if(items!=null && items.size()>0){
           for (CartItemInfoVo item : items) {
               countNum+=item.getCount();
           }
       }
        return countNum;
    }


    public Integer getCountType() {
        countType=0;
        if(items!=null && items.size()>0){
            for (CartItemInfoVo item : items) {
                countType+=1;
            }
        }
        return countType;
    }


    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        //1、计算购物项总价
        if (items != null && items.size() > 0) {
            for (CartItemInfoVo item : items) {
                if(item.getCheck()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }

        //2、减去优惠总价
        BigDecimal subtract = amount.subtract(getReduce());

        return subtract;
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
