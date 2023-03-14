package com.home.common.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: lyq
 * @createDate: 14/3/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockTo {

    private Long wareOrderId;
    private WareOrderDetailTo wareOrderDetailTo;
}
