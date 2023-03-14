package com.home.mall.order.feign;

import com.home.mall.order.vo.SpuInfoRespVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @description:
 * @author: lyq
 * @createDate: 23/2/2023
 * @version: 1.0
 */
@FeignClient("mall-product")
public interface ProductFeignService {
    @GetMapping("/product/spuinfo/{skuId}/getSpuInfo")
    SpuInfoRespVo getSpuInfo(@PathVariable("skuId")Long skuId);
}
