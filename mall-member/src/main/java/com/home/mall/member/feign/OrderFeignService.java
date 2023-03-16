package com.home.mall.member.feign;

import com.home.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @description:
 * @author: lyq
 * @createDate: 16/3/2023
 * @version: 1.0
 */

@FeignClient("mall-order")
public interface OrderFeignService {
    @PostMapping("/order/order/listWith")
    R listWith(@RequestBody Map<String, Object> params);
}
