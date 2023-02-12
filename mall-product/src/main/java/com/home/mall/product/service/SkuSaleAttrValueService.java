package com.home.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.product.entity.SkuSaleAttrValueEntity;
import com.home.mall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuSaleAttrValue(List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities);

    List<SkuItemVo.SkuItemSaleAttrVo> getSpuSaleAttrBySpuId(Long spuId);

    List<String> getProductAttributesList(Long skuId);
}

