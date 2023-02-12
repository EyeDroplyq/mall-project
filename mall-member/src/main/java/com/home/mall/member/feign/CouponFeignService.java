package com.home.mall.member.feign;

import com.home.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: lyq
 * @createDate: 10/1/2023
 * @version: 1.0
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/coupons/list")
    public R couponsList();

}
