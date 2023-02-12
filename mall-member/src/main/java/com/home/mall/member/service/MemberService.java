package com.home.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.member.entity.MemberEntity;
import com.home.mall.member.exception.PhoneExistException;
import com.home.mall.member.exception.UserNameExistException;
import com.home.mall.member.vo.MemberLoginVo;
import com.home.mall.member.vo.MemberRegistVo;
import com.home.mall.member.vo.SocialUserVo;

import java.util.Map;

/**
 * 会员
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 22:38:50
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);

    void checkUserName(String userName) throws UserNameExistException;

    void checkPhone(String phone) throws PhoneExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity oauthLogin(SocialUserVo vo) throws Exception;
}

