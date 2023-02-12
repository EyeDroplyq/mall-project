package com.home.mall.cart.controller;

import com.home.common.constant.AuthServerConstant;
import com.home.mall.cart.interceptor.CartInterceptor;
import com.home.mall.cart.service.CartService;
import com.home.mall.cart.to.UserInfoTo;
import com.home.mall.cart.vo.CartItemInfoVo;
import com.home.mall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: lyq
 * @createDate: 8/2/2023
 * @version: 1.0
 */
@Controller
public class CartController {
    @Autowired
    private CartService cartService;


    @GetMapping(value = "/deleteItem")
    public String deleteCartItem(@RequestParam("skuId")Long skuId){
        cartService.deleteCartItem(skuId);
        return "redirect:http://cart.mall.com/cart.html";
    }


    @GetMapping(value = "/countItem")
    public String countItem(@RequestParam("skuId")Long skuId,@RequestParam("num")Integer num){
        cartService.countItem(skuId,num);
        return "redirect:http://cart.mall.com/cart.html";
    }


    @GetMapping(value = "/checkItem")
    public String checkItem(@RequestParam("skuId")Long skuId,@RequestParam("check")Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.mall.com/cart.html";
    }


    /**
     * 点击购物车跳转到购物车页面的controller,获取整个购物车的信息
     * @return
     */
    @GetMapping(value = "/cart.html")
    public String cartListPage(Model model){
        CartVo cart=cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    @GetMapping(value = "/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes){
        CartItemInfoVo carItem=cartService.addToCart(skuId,num);
        //为了防止我们在添加购物车成功的页面刷新的时候重复提交订单，我们重定向过去，然后将数据显示即可
        redirectAttributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.mall.com/addToCartSuccess.html";
    }

    @GetMapping(value = "/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId")Long skuId,Model model){
        CartItemInfoVo item=cartService.showAddToCartSuccess(skuId);
        model.addAttribute("item",item);
        return "success";
    }


}
