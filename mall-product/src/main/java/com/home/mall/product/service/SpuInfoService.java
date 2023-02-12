package com.home.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.product.entity.SpuInfoEntity;
import com.home.mall.product.vo.SpuSaveVo;

import java.io.IOException;
import java.util.Map;

/**
 * spu信息
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils querySpuInfo(Map<String, Object> params);

    void up(Long spuId) throws IOException;
}

