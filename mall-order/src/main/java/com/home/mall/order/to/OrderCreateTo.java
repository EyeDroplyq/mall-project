package com.home.mall.order.to;

import com.home.mall.order.entity.OrderEntity;
import com.home.mall.order.entity.OrderItemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 23/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateTo {
    private OrderEntity order;//订单实体类
    private List<OrderItemEntity> orderItems; //订单项列表
    private BigDecimal payPrice;//应付价格
    private BigDecimal fare=new BigDecimal("0"); //运费
}
