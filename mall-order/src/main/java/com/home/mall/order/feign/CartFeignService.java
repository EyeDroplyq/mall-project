package com.home.mall.order.feign;

import com.home.mall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/2/2023
 * @version: 1.0
 */
@FeignClient("mall-cart")
public interface CartFeignService {
    @GetMapping("/getCartItems")
    List<OrderItemVo> getCartItems();
}
