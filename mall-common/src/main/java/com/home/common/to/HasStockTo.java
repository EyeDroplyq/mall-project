package com.home.common.to;

import lombok.Data;

/**
 * @description: 是否有库存的to
 * @author: lyq
 * @createDate: 24/1/2023
 * @version: 1.0
 */
@Data
public class HasStockTo {

    private Long skuId;

    private Boolean hasStock;
}
