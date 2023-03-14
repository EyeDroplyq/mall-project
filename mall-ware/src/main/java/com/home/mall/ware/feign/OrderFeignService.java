package com.home.mall.ware.feign;

import com.home.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @description:
 * @author: lyq
 * @createDate: 14/3/2023
 * @version: 1.0
 */
@FeignClient("mall-order")
public interface OrderFeignService {
    @GetMapping("/order/order/getOrderStatus/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
