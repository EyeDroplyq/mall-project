package com.home.mall.search.feign;

import com.home.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: lyq
 * @createDate: 30/1/2023
 * @version: 1.0
 */

@FeignClient("mall-product")
public interface ProductFeignService {

    @GetMapping("/product/attr/info/{attrId}")
    R info(@PathVariable("attrId") Long attrId);

}
