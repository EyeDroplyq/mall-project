package com.home.mall.order.web;

import com.alipay.api.AlipayApiException;
import com.home.common.utils.R;
import com.home.mall.order.config.AliPayTemplate;
import com.home.mall.order.service.OrderService;
import com.home.mall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: lyq
 * @createDate: 15/3/2023
 * @version: 1.0
 */
@Controller
public class PayWebController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AliPayTemplate aliPayTemplate;

    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String pay(@RequestParam("orderSn")String orderSn) throws AlipayApiException {
        PayVo payVo=orderService.getPayInfoByOrderSn(orderSn);
        String pay = aliPayTemplate.pay(payVo);
        return pay;
    }
}
