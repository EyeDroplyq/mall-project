package com.home.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.product.entity.AttrEntity;
import com.home.mall.product.entity.AttrGroupEntity;
import com.home.mall.product.vo.AttrAndAttrResGroupVo;
import com.home.mall.product.vo.AttrAttrGroupRelationVo;
import com.home.mall.product.vo.SkuItemVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPage(Map<String, Object> params,Long catelogId);

    List<AttrEntity> getAllAttr(Long attrGroupId);

    void saveAttrAttrGroupRelation(AttrAttrGroupRelationVo[] relationVos);

    void removeAttrAttrGroupRelation(AttrAttrGroupRelationVo[] relationVo);

    List<AttrAndAttrResGroupVo> getAttrAndAttrGroupByCatId(Long catelogId);

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrAndAttrGroupByCatalogIdAndSpuId(Long catalogId, Long spuId);
}

