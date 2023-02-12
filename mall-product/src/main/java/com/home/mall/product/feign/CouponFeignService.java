package com.home.mall.product.feign;

import com.home.common.to.SkuReductionTo;
import com.home.common.to.SpuBoundTo;
import com.home.common.utils.R;
import com.home.mall.product.vo.Bounds;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @author: lyq
 * @createDate: 16/1/2023
 * @version: 1.0
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {
//    @PostMapping(value = "/coupon/spubounds/save")
//    public R saveSpuBound(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
