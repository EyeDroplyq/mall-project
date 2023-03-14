package com.home.mall.ware.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 锁库存的实体类
 * @author: lyq
 * @createDate: 24/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WareSkuLock {
    private String orderSn;//订单号
    private List<OrderItemVo> locks;//锁住的所有库存信息
}
