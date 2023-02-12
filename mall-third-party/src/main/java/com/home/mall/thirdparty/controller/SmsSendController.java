package com.home.mall.thirdparty.controller;

import com.home.common.utils.R;
import com.home.mall.thirdparty.component.SmsCompoent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    private SmsCompoent smsCompoent;

    @GetMapping(value = "/sendcode")
    public R sendSms(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsCompoent.sendSmsCode(phone,code);
        return R.ok();
    }

}
