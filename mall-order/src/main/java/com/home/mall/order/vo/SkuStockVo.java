package com.home.mall.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: lyq
 * @createDate: 21/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuStockVo {
    private Long skuId;
    private Boolean hasStock;
}
