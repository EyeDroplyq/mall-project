package com.home.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.home.common.constant.ProductConstant;
import com.home.mall.product.dao.AttrAttrgroupRelationDao;
import com.home.mall.product.dao.AttrGroupDao;
import com.home.mall.product.dao.CategoryDao;
import com.home.mall.product.entity.*;
import com.home.mall.product.service.CategoryService;
import com.home.mall.product.service.ProductAttrValueService;
import com.home.mall.product.vo.AttrResponseVo;
import com.home.mall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.product.dao.AttrDao;
import com.home.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Resource
    private AttrAttrgroupRelationDao relationDao;
    @Resource
    private CategoryDao categoryDao;
    @Resource
    private AttrGroupDao attrGroupDao;
    @Resource
    private CategoryService categoryService;
    @Resource
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询分类对应的属性
     *
     * @param params
     * @param catelogId
     * @param type
     * @return
     */
    @Transactional
    @Override
    public PageUtils queryAttr(Map<String, Object> params, Long catelogId, String type) {
//        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type","base".equalsIgnoreCase(type)?ProductConstant.AttrEnum.ATTR_BASE_TYPE.getCode():ProductConstant.AttrEnum.ATTR_SALE_TYPE.getCode());
//
//        if (catelogId != 0) {
//            queryWrapper.eq("catelog_id", catelogId);
//        }
//        String key = (String) params.get("key");
//        if (!StringUtils.isEmpty(key)) {
//            queryWrapper.and((wrapper) -> {
//                wrapper.eq("attr_id", key).or().like("attr_name", key);
//            });
//        }
//        IPage<AttrEntity> page = this.page(
//                new Query<AttrEntity>().getPage(params),
//                queryWrapper
//        );
//
//        PageUtils pageUtils = new PageUtils(page);
//        //得到所有的返回数据
//        List<AttrEntity> records = page.getRecords();
//        List<AttrResponseVo> resList = records.stream().map((attrEntity) -> {
//            AttrResponseVo attrResponseVo = new AttrResponseVo();
//            BeanUtils.copyProperties(attrEntity, attrResponseVo);
//            AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
//            if("base".equalsIgnoreCase(type)){
//                if (attrId != null) {
//                    Long groupId = attrId.getAttrGroupId();
//                    if(groupId!=null){
//                        AttrGroupEntity groupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
//                        attrResponseVo.setGroupName(groupEntity.getAttrGroupName());
//                    }
//
//                }
//            }
//            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
//            if (categoryEntity != null) {
//                attrResponseVo.setCatelogName(categoryEntity.getName());
//            }
//            return attrResponseVo;
//        }).collect(Collectors.toList());
//        pageUtils.setList(resList);
//        return pageUtils;
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type","base".equalsIgnoreCase(type)?ProductConstant.AttrEnum.ATTR_BASE_TYPE.getCode():ProductConstant.AttrEnum.ATTR_SALE_TYPE.getCode());

        if(catelogId != 0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        if(catelogId==0){
            IPage<AttrEntity> page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    queryWrapper
            );
            return new PageUtils(page);
        }

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            //attr_id  attr_name
            queryWrapper.and((wrapper)->{
                wrapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVo> respVos = records.stream().map((attrEntity) -> {
            AttrResponseVo attrRespVo = new AttrResponseVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //1、设置分类和分组的名字
            if("base".equalsIgnoreCase(type)){
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrId != null && attrId.getAttrGroupId()!=null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }

            }


            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }


    /**
     * 保存属性，以及级联的保存属性和分组的关系
     *
     * @param attr
     */
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        if(attr.getAttrType()== ProductConstant.AttrEnum.ATTR_BASE_TYPE.getCode() && attr.getAttrGroupId()!=null){
            //保存属性分组信息
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrGroupId(attr.getAttrGroupId());
            entity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(entity);
        }
    }

    /**
     * 查询属性详情，可以用来进行修改时回显
     *
     * @param attrId
     * @return
     */
    @Cacheable(value = "attr",key = "'attrinfo:'+#root.args[0]")
    @Override
    public AttrResponseVo getAttr(Long attrId) {
//        AttrResponseVo attrResponseVo = new AttrResponseVo();
//        AttrEntity attrEntity = this.getById(attrId);
//        if(attrEntity!=null){
//            BeanUtils.copyProperties(attrEntity, attrResponseVo);
//            if(attrEntity.getAttrType()==ProductConstant.AttrEnum.ATTR_BASE_TYPE.getCode()){
//                //根据属性id查询属性-分组表实体
//                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
//                if (relationEntity != null) {
//                    //根据属性-分组关系表查询分组的信息
//                    AttrGroupEntity groupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
//                    if (groupEntity != null) {
//                        //给返回实体类设置分组id
//                        attrResponseVo.setAttrGroupId(groupEntity.getAttrGroupId());
//                        //给返回实体类设置分组名字
//                        attrResponseVo.setGroupName(groupEntity.getAttrGroupName());
//                    }
//                    Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
//                    //给响应实体vo设置分类路径
//                    attrResponseVo.setCatelogPath(catelogPath);
//                    //查询分类实体
//                    CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
//                    if(categoryEntity!=null){
//                        //给返回实体设置分类名称
//                        attrResponseVo.setCatelogName(categoryEntity.getName());
//                    }
//                }
//            }else{
//                Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
//                //给响应实体vo设置分类路径
//                attrResponseVo.setCatelogPath(catelogPath);
//            }
//
//        }
//
//        return attrResponseVo;
        AttrResponseVo respVo = new AttrResponseVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,respVo);



        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_BASE_TYPE.getCode()){
            //1、设置分组信息
            AttrAttrgroupRelationEntity attrgroupRelation = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if(attrgroupRelation!=null){
                respVo.setAttrGroupId(attrgroupRelation.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelation.getAttrGroupId());
                if(attrGroupEntity!=null){
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }


        //2、设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        respVo.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if(categoryEntity!=null){
            respVo.setCatelogName(categoryEntity.getName());
        }


        return respVo;
    }

    /**
     * 修改属性信息
     * @param attr
     */
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        //更新自己信息
        this.updateById(attrEntity);
        if(attr.getAttrType()==ProductConstant.AttrEnum.ATTR_BASE_TYPE.getCode()){
            //更新关联的数据信息,当改变属性的组的时候，属性-分组关系表也需要更新
            Long attrGroupId = attr.getAttrGroupId();
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            //根据属性id，更新属性分组id
            relationDao.update(relationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
        }

    }

    /**
     * 查询没有被关联的属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //该组只能关联自己所属分类下的其他属性


        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //得到当前这个组所属分类id
        Long catelogId = attrGroupEntity.getCatelogId();

        //查询当前这个分类下的其他组
        List<AttrGroupEntity> groupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //查询其他所有组所选择的属性
        //得到其他组所的所有组id
        List<Long> attrGroupIds = groupEntities.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIds));
        //得到其他所有组所引用的属性id
        List<Long> attrIds = relationEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //前端穿过来的这个组不能选择属性id属于attrIds里面的
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        if(attrIds!=null){
            queryWrapper.notIn("attr_id",attrIds);
        }
        queryWrapper.eq("catelog_id",catelogId).ne("attr_type",0);
//        List<AttrEntity> attrEntities = this.baseMapper.selectList(queryWrapper);
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    /**
     * 根据spuid查询商品对应的属性
     * @param spuId
     * @return
     */
    @Override
    public List<ProductAttrValueEntity> getSpuAttrs(Long spuId) {
        QueryWrapper<ProductAttrValueEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id",spuId);
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.list(queryWrapper);
        return productAttrValueEntities;
    }

    /**
     * 更新spu规格参数
     * @param spuId
     */
    @Override
    public void updateSpuAttrs(Long spuId,List<ProductAttrValueEntity> entities) {
        //1.先根据spuId查出对应的productAttrValue实体类
        QueryWrapper<ProductAttrValueEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id",spuId);
       //将原来的删除
        productAttrValueService.remove(queryWrapper);
        //将修改的插入
        List<ProductAttrValueEntity> collect = entities.stream().map(item -> {
            item.setSpuId(spuId);
            item.setAttrSort(0);
            return item;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);
    }

    /**
     * 通过传过来的attrid列表，查询所有的可以检索的属性，将他们的id返回
     * @param attrIds
     * @return
     */
    @Override
    public List<Long> getAllCanSearchIdsByAttrIds(List<Long> attrIds) {
        return baseMapper.getAllCanSearchIdsByAttrIds(attrIds);
    }

}