package com.home.mall.product.service.impl;

import com.home.mall.product.vo.SkuItemVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.product.dao.SkuSaleAttrValueDao;
import com.home.mall.product.entity.SkuSaleAttrValueEntity;
import com.home.mall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuSaleAttrValue(List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities) {
        this.saveBatch(skuSaleAttrValueEntities);
    }

    /**
     * 通过spu查询该spu对应的所有的销售属性分组
     * @param spuId
     * @return
     */
    @Override
    public List<SkuItemVo.SkuItemSaleAttrVo> getSpuSaleAttrBySpuId(Long spuId) {
        List<SkuItemVo.SkuItemSaleAttrVo> saleAttrVos=baseMapper.getSpuSaleAttrBySpuId(spuId);
        return saleAttrVos;
    }

    /**
     * 根据skuId获取销售属性list
     * @param skuId
     * @return
     */
    @Override
    public List<String> getProductAttributesList(Long skuId) {
        return this.baseMapper.getProductAttributesList(skuId);

    }

}