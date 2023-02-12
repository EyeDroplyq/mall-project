package com.home.mall.product.service.impl;

import com.home.mall.product.entity.SpuImagesEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.product.dao.SkuImagesDao;
import com.home.mall.product.entity.SkuImagesEntity;
import com.home.mall.product.service.SkuImagesService;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuImages(List<SkuImagesEntity> skuImagesEntities) {
        this.saveBatch(skuImagesEntities);
    }

    /**
     * 通过skuid获得所有的sku照片信息
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
        List<SkuImagesEntity> images = baseMapper.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        return images;
    }


}