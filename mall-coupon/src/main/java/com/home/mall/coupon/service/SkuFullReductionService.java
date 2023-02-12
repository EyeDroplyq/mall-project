package com.home.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.to.SkuReductionTo;
import com.home.common.utils.PageUtils;
import com.home.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 22:32:06
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAllInfo(SkuReductionTo skuReductionTo);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

