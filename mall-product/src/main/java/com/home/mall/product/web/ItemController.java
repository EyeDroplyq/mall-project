package com.home.mall.product.web;

import com.home.mall.product.service.SkuInfoService;
import com.home.mall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;

/**
 * @description:
 * @author: lyq
 * @createDate: 31/1/2023
 * @version: 1.0
 */
@Controller
public class ItemController {
    @Resource
    private SkuInfoService skuInfoService;
    @GetMapping(value = "/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model){

        SkuItemVo vo=skuInfoService.item(skuId);
        model.addAttribute("item",vo);
        return "item";
    }
}
