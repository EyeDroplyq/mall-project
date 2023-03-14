package com.home.mall.ware.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: lyq
 * @createDate: 24/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private Boolean locked;
}
