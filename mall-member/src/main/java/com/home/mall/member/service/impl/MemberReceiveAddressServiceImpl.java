package com.home.mall.member.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.member.dao.MemberReceiveAddressDao;
import com.home.mall.member.entity.MemberReceiveAddressEntity;
import com.home.mall.member.service.MemberReceiveAddressService;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberReceiveAddressEntity> getUserAddress(Long userId) {
        List<MemberReceiveAddressEntity> address = this.baseMapper.selectList(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", userId));
        return address;
    }

    @Override
    public MemberReceiveAddressEntity getReceiveMember(Long addrId) {
        MemberReceiveAddressEntity memberReceiveAddressEntity = this.baseMapper.selectById(addrId);
        return memberReceiveAddressEntity;
    }

}