package com.home.mall.cart.feign;

import com.home.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 8/2/2023
 * @version: 1.0
 */
@FeignClient("mall-product")
public interface ProductServiceFeign {
    @GetMapping("/product/skuinfo/info/{skuId}")
     R info(@PathVariable("skuId") Long skuId);

    @GetMapping(value = "/product/skusaleattrvalue/getSaleAttrList/{skuId}")
    List<String> getProductAttributesList(@PathVariable("skuId")Long skuId);
}
