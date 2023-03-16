package com.home.mall.member.web;

import com.home.common.utils.R;
import com.home.mall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: lyq
 * @createDate: 16/3/2023
 * @version: 1.0
 */
@Controller
public class MemberWebController {
    @Autowired
    private OrderFeignService orderFeignService;
    @GetMapping(value = "/memberOrder.html")
    public String getOrderListPage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum, Model model){
        Map<String,Object> params=new HashMap<>();
        params.put("page",pageNum.toString());
        R r = orderFeignService.listWith(params);
        model.addAttribute("orders",r);
        //将用户的订单信息查询出来，然后进行显示
        return "orderList";
    }
}
