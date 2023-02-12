package com.home.mall.product.dao;

import com.home.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.home.mall.product.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrAndAttrGroupByCatalogIdAndSpuId(@Param("catalogId") Long catalogId, @Param("spuId") Long spuId);
}
