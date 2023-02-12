package com.home.mall.product.feign;

import com.home.common.to.es.SkuEsModel;
import com.home.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 24/1/2023
 * @version: 1.0
 */
@FeignClient("mall-search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    public R skuSave(@RequestBody List<SkuEsModel> esModels);

}
