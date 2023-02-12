package com.home.mall.product.feign;

import com.home.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 24/1/2023
 * @version: 1.0
 */
@FeignClient("mall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasstock")
    R hasStock(@RequestBody List<Long> skuIds);
}
