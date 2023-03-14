package com.home.mall.order.vo;

import com.home.mall.order.entity.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 提交订单后的返回实体
 * @author: lyq
 * @createDate: 23/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSubmitRespVo {
    private OrderEntity order;
    private Integer code;//0是成功     其他是失败
}
