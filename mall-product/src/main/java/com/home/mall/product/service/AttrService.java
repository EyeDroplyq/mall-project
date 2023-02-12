package com.home.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.product.entity.AttrEntity;
import com.home.mall.product.entity.ProductAttrValueEntity;
import com.home.mall.product.vo.AttrResponseVo;
import com.home.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryAttr(Map<String, Object> params, Long catelogId, String type);

    void saveAttr(AttrVo attr);

    AttrResponseVo getAttr(Long attrId);

    void updateAttr(AttrVo attr);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    List<ProductAttrValueEntity> getSpuAttrs(Long spuId);

    void updateSpuAttrs(Long spuId,List<ProductAttrValueEntity> entities);

    List<Long> getAllCanSearchIdsByAttrIds(List<Long> attrIds);
}

