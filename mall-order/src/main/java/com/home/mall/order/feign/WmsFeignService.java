package com.home.mall.order.feign;

import com.home.common.utils.R;
import com.home.mall.order.vo.WareSkuLock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 21/2/2023
 * @version: 1.0
 */
@FeignClient("mall-ware")
public interface WmsFeignService {
    @PostMapping("/ware/waresku/hasstock")
    R hasStock(@RequestBody List<Long> skuIds);

    @PostMapping("/ware/waresku/lock/order")
    R lockOrder(WareSkuLock vo);
}
