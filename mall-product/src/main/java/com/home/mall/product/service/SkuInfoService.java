package com.home.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.product.entity.SkuInfoEntity;
import com.home.mall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils querySkuInfo(Map<String, Object> params);

    List<SkuInfoEntity> getAllSkuInfoBySpuId(Long spuId);

    SkuItemVo item(Long skuId);
}

