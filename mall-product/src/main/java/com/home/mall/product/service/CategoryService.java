package com.home.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.product.entity.CategoryEntity;
import com.home.mall.product.vo.CataLogLevel2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    Long[] findCatelogPath(Long catlogId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getFirstCategory();

    Map<String, List<CataLogLevel2Vo>> cataLogJson();
}

