package com.home.mall.auth.feign;

import com.home.common.utils.R;
import com.home.mall.auth.vo.SocialUserVo;
import com.home.mall.auth.vo.UserLoginVo;
import com.home.mall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
@FeignClient("mall-member")
public interface MemberServerFeign {
    @PostMapping(value = "/member/member/regist")
    R registerMember(@RequestBody UserRegistVo vo);

    @PostMapping(value = "/member/member/login")
    R loginMember(@RequestBody UserLoginVo vo);

    @PostMapping(value = "/member/member/oauth/login")
    R oauthLogin(@RequestBody SocialUserVo vo) throws Exception;
}
