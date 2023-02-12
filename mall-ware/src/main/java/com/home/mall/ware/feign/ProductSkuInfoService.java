package com.home.mall.ware.feign;

import com.home.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: lyq
 * @createDate: 17/1/2023
 * @version: 1.0
 */
@FeignClient("mall-product")
public interface ProductSkuInfoService {
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
