package com.home.mall.auth.feign;

import com.home.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
@FeignClient("mall-third-party")
public interface ThirdPartServiceFeign {

    @GetMapping(value = "/sms/sendcode")
    public R sendSms(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
