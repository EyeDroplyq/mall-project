package com.home.mall.product.service.impl;


import com.home.mall.product.dao.AttrAttrgroupRelationDao;
import com.home.mall.product.dao.AttrDao;
import com.home.mall.product.entity.AttrAttrgroupRelationEntity;
import com.home.mall.product.entity.AttrEntity;
import com.home.mall.product.service.AttrService;
import com.home.mall.product.vo.AttrAndAttrResGroupVo;
import com.home.mall.product.vo.AttrAttrGroupRelationVo;
import com.home.mall.product.vo.SkuItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.product.dao.AttrGroupDao;
import com.home.mall.product.entity.AttrGroupEntity;
import com.home.mall.product.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Resource
    private AttrAttrgroupRelationDao relationDao;
    @Resource
    private AttrDao attrDao;
    @Resource
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    //根据三级分类id来查询对应的属性分组信息
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId == 0) { //如果三级分类id为0的话，查询所有
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        }else{ //如果传过来分级分类得id了
            //对应查询的sql语句为 select * from pms_attr_group where catelogId=? AND (attr_group_id=key or attr_group_name like key)
            //前端可以进行检索查询，回传过来一个key，这个key可能是attr_group_id，也可能是attr_group_name
            String key = (String) params.get("key");
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
            wrapper.eq("catelog_id",catelogId);
            if(!StringUtils.isEmpty(key)){
                //如果进行了检索,封装查询条件
                wrapper.and((obj)->{
                    obj.eq("attr_group_id", key).or().like("attr_group_name",key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据属性组的id查询这个组对应的所有的属性信息
     * @param attrGroupId
     * @return
     */
    @Transactional
    @Override
    public List<AttrEntity> getAllAttr(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        List<Long> ids = relationEntities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(ids);
        return attrEntities;
    }

    /**
     * 添加属性和属性分组的关联关系  注：一个属性分组对应多个属性
     * @param relationVos
     */
    @Override
    public void saveAttrAttrGroupRelation(AttrAttrGroupRelationVo[] relationVos) {
        List<AttrAttrGroupRelationVo> vos = Arrays.asList(relationVos);
        for (AttrAttrGroupRelationVo vo : vos) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(vo.getAttrId());
            relationEntity.setAttrGroupId(vo.getAttrGroupId());
            relationDao.insert(relationEntity);
        }
    }

    /**
     * 批量删除属性和属性分组关系
     * @param relationVo
     */
    @Override
    public void removeAttrAttrGroupRelation(AttrAttrGroupRelationVo[] relationVo) {
        List<AttrAttrgroupRelationEntity> relationEntities = Arrays.asList(relationVo).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.batchDeleteRelation(relationEntities);
    }

    /**
     * 通过分类id查询该分类下的所有组以及这些组对应的属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrAndAttrResGroupVo> getAttrAndAttrGroupByCatId(Long catelogId) {
       //1.查询这个分类id对应的所有组
        List<AttrGroupEntity> groups = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<AttrAndAttrResGroupVo> collect = groups.stream().map((item) -> {
            AttrAndAttrResGroupVo attrAndAttrResGroupVo = new AttrAndAttrResGroupVo();
            //2.将每个组的实体类属性复制到vo中
            BeanUtils.copyProperties(item, attrAndAttrResGroupVo);
            //查询该组就应的所有的属性
            List<AttrEntity> attrs = this.getAllAttr(item.getAttrGroupId());
            attrAndAttrResGroupVo.setAttrs(attrs);
            return attrAndAttrResGroupVo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 通过三级分类的id以及spu的id来查询sku对应的所有的属性分组以及属性分组中的属性值
     * @param catalogId
     * @param spuId
     * @return
     */
    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrAndAttrGroupByCatalogIdAndSpuId(Long catalogId, Long spuId) {
        List<SkuItemVo.SpuItemAttrGroupVo> vos=baseMapper.getAttrAndAttrGroupByCatalogIdAndSpuId(catalogId,spuId);
        return vos;
    }


}