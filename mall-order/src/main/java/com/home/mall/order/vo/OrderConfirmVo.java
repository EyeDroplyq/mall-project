package com.home.mall.order.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/2/2023
 * @version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
//订单确认页的vo
public class OrderConfirmVo {
    @Setter @Getter
    private List<MemberAddressVo> address;
    @Setter @Getter
    private List<OrderItemVo> items;
    //优惠券信息...
    @Setter @Getter
    Integer integration;

    @Setter @Getter
    Map<Long,Boolean> stocks;


    //防重令牌
    @Setter @Getter
    String orderToken;

    public Integer getCount(){
        Integer i = 0 ;
        if(items!=null){
            for (OrderItemVo item : items) {
                i+=item.getCount();
            }
        }
        return i;
    }

//    BigDecimal total;//订单总额

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if(items!=null){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }

        return sum;
    }

//    BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        return  getTotal();
    }

}
