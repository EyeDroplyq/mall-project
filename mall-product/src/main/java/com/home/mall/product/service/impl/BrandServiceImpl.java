package com.home.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.home.mall.product.entity.CategoryBrandRelationEntity;
import com.home.mall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.product.dao.BrandDao;
import com.home.mall.product.entity.BrandEntity;
import com.home.mall.product.service.BrandService;

import javax.annotation.Resource;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");//获取进行模糊查询的字段
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){ //如果进行了模糊查询
            wrapper.like("brand_id",key).or().like("name",key);
            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }else{ //如果没有进行模糊查询，则查询所有
            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    new QueryWrapper<BrandEntity>()
            );

            return new PageUtils(page);
        }

    }

    /**
     * 更新自己以及更新与他关联的表
     * @param brand
     */
    @Override
    public void updateBDetail(BrandEntity brand) {
        //更新自己
        this.updateById(brand);
        if(!StringUtils.isEmpty(brand.getName())){ //如果品牌名字发生改变了，也需要更新与他关联的表
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
        }
    }

}