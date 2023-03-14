package com.home.mall.order.web;

import com.home.mall.order.service.OrderService;
import com.home.mall.order.vo.OrderConfirmVo;
import com.home.mall.order.vo.OrderSubmitRespVo;
import com.home.mall.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/2/2023
 * @version: 1.0
 */

@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/toTrade")
    public String toTrade(Model model, HttpServletRequest request){
        OrderConfirmVo confirmVo=orderService.orderConfirm();
        model.addAttribute("orderConfirmData",confirmVo);
        return "confirm";
    }


    /**
     * 提交订单之后的业务处理
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder( OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        OrderSubmitRespVo orderSubmitRespVo=orderService.submitOrder(vo);
        if(orderSubmitRespVo.getCode()==0){
            //提交订单成功的话，跳转到支付页
            model.addAttribute("submitOrderResp",orderSubmitRespVo);
            return "pay";
        }else{
            String msg="创建订单失败:";
            //提交订单失败
            switch (orderSubmitRespVo.getCode()){
                case 1: msg+="重复下单"; break;
                case 2: msg+="价格不匹配"; break;
                case 3: msg+="库存不足"; break;
            }
            redirectAttributes.addFlashAttribute("msg",msg);
            return "redirect:http://order.mall.com/toTrade";
        }
    }
}
