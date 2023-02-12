package com.home.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 22:38:50
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

