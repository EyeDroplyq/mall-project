package com.home.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.home.common.exception.BizErrorEnum;
import com.home.mall.member.exception.PhoneExistException;
import com.home.mall.member.exception.UserNameExistException;
import com.home.mall.member.feign.CouponFeignService;
import com.home.mall.member.vo.MemberLoginVo;
import com.home.mall.member.vo.MemberRegistVo;
import com.home.mall.member.vo.SocialUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.home.mall.member.entity.MemberEntity;
import com.home.mall.member.service.MemberService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;

import javax.annotation.Resource;


/**
 * 会员
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 22:38:50
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Resource
    private CouponFeignService couponFeignService;

    //测试方法
    @RequestMapping("/couponsList")
    public R memberCoupons(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("Tom");
        R couponsList = couponFeignService.couponsList();
        return R.ok().put("member",memberEntity).put("coupons",couponsList.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 注册功能
     * @param vo
     * @return
     */
    @PostMapping(value = "/regist")
    public R registerMember(@RequestBody MemberRegistVo vo){
        try {
            memberService.regist(vo);
        }catch (UserNameExistException e){
        //如果有异常根据不同的异常，返回不同的状态信息
            return R.error(BizErrorEnum.USERNAME_EXIST_EXCEPTION.getCode(), BizErrorEnum.USERNAME_EXIST_EXCEPTION.getMsg());
        }catch (PhoneExistException e){
            return R.error(BizErrorEnum.PHONE_EXIST_EXCEPTION.getCode(),BizErrorEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }


    @PostMapping(value = "/login")
    public R loginMember(@RequestBody MemberLoginVo vo){
        MemberEntity member=memberService.login(vo);
        if(member!=null){
            return R.ok().setDate(member);
        }else{
            return R.error(BizErrorEnum.USER_LOGIN_EXCEPTION.getCode(), BizErrorEnum.USER_LOGIN_EXCEPTION.getMsg());
        }
    }

    /**
     * 第三方登录功能
     * @param vo
     * @return
     */
    @PostMapping(value = "/oauth/login")
    public R oauthLogin(@RequestBody SocialUserVo vo) throws Exception {
        MemberEntity member=memberService.oauthLogin(vo);
        if(member!=null){
            return R.ok().setDate(member);
        }else{
            return R.error(BizErrorEnum.USER_LOGIN_EXCEPTION.getCode(), BizErrorEnum.USER_LOGIN_EXCEPTION.getMsg());
        }
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
