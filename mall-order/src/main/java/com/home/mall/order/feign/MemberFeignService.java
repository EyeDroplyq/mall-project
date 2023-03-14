package com.home.mall.order.feign;

import com.home.mall.order.vo.MemberAddressVo;
import com.home.mall.order.vo.MemberReceiveAddressEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/2/2023
 * @version: 1.0
 */
@FeignClient("mall-member")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/{userId}/getUserAddress")
    List<MemberAddressVo> address(@PathVariable("userId")Long userId);

    //根据地址id来获取收货人的信息
    @GetMapping("/member/memberreceiveaddress/{addrId}/getReceiveMember")
    MemberReceiveAddressEntity getReceiveMember(@PathVariable("addrId")Long addrId);
}
