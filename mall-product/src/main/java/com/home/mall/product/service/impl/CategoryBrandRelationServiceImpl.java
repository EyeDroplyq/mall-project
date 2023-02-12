package com.home.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.home.mall.product.dao.BrandDao;
import com.home.mall.product.dao.CategoryDao;
import com.home.mall.product.entity.BrandEntity;
import com.home.mall.product.entity.CategoryEntity;
import com.home.mall.product.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.product.dao.CategoryBrandRelationDao;
import com.home.mall.product.entity.CategoryBrandRelationEntity;
import com.home.mall.product.service.CategoryBrandRelationService;

import javax.annotation.Resource;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Resource
    private CategoryDao categoryDao;
    @Resource
    private BrandDao brandDao;

    @Resource
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存品牌分类关系
     * @param categoryBrandRelation
     */
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        //因为前端只传过来brandId 和 categordId 所以我们需要查出分别对应的品牌名和分类名然后再保存
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

    /**
     * 当品牌表更改的时候，更新关联表中的数据
     * @param brandId
     * @param name
     */
    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
        relationEntity.setBrandId(brandId);
        relationEntity.setBrandName(name);
        this.update(relationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    //更新品牌分类关系表中的分类信息
    @Override
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId,name);
    }

    /**
     * 根据分类查询分类下的所有品牌
     * @param catId
     * @return
     */
    @Override
    public List<BrandEntity> getBrand(Long catId) {
        List<CategoryBrandRelationEntity> relationEntities = this.baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<Long> brandIds = relationEntities.stream().map((item) -> {
            Long brandId = item.getBrandId();
            return brandId;
        }).collect(Collectors.toList());
        List<BrandEntity> brandEntities = brandDao.selectBatchIds(brandIds);
        return brandEntities;
    }


}