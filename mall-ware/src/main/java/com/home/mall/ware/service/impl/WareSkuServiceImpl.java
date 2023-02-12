package com.home.mall.ware.service.impl;

import com.home.common.to.HasStockTo;
import com.home.common.utils.R;
import com.home.mall.ware.feign.ProductSkuInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.ware.dao.WareSkuDao;
import com.home.mall.ware.entity.WareSkuEntity;
import com.home.mall.ware.service.WareSkuService;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Resource
    ProductSkuInfoService productSkuInfoService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 采购完成时更新库存信息
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    @Override
    public void updateStock(Long skuId, Long wareId, Integer skuNum) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id",skuId).eq("ware_id",wareId);
        List<WareSkuEntity> wareSkuEntities = this.list(queryWrapper);
        if(wareSkuEntities==null || wareSkuEntities.size()==0){ //如果库存信息中没有这个库存的话，就是保存操作
            //保存操作
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            try {
                //远程调用，保存skuname信息
                R info = productSkuInfoService.info(skuId);
                Map<String,Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode()==0){
                    String skuName = (String) skuInfo.get("skuName");
                    wareSkuEntity.setSkuName(skuName);
                }
            }catch (Exception e){

            }
            this.save(wareSkuEntity);
        }else{
            //否则为更新操作
            this.baseMapper.updateStock(skuId,wareId,skuNum);
        }
    }

    /**
     * 查询对应的sku是否有库存，返回的结果是 skuid,hasStock
     * @param skuIds
     * @return
     */
    @Override
    public List<HasStockTo> hasStock(List<Long> skuIds) {
        List<HasStockTo> collect = skuIds.stream().map(id -> {
            HasStockTo hasStockTo = new HasStockTo();
            hasStockTo.setSkuId(id);
            Long count = baseMapper.hasStock(id);
            hasStockTo.setHasStock(count == null ? false : count > 0);
            return hasStockTo;
        }).collect(Collectors.toList());
        return collect;
    }

}