package com.home.mall.product.service.impl;

import com.home.mall.product.entity.SkuImagesEntity;
import com.home.mall.product.entity.SpuInfoDescEntity;
import com.home.mall.product.service.*;
import com.home.mall.product.vo.SkuItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.product.dao.SkuInfoDao;
import com.home.mall.product.entity.SkuInfoEntity;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Resource
    private SkuImagesService imagesService;
    @Resource
    private SpuInfoDescService spuInfoDescService;
    @Resource
    private AttrGroupService groupService;
    @Resource
    private SkuSaleAttrValueService saleAttrValueService;
    @Resource
    private ThreadPoolExecutor executor;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    /**
     * 检索sku信息
     * @param params
     * @return
     */
    @Override
    public PageUtils querySkuInfo(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){ //如果进行了模糊查询
            queryWrapper.and(wrapper->{
                wrapper.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){ //如果进行了分类id查询
            queryWrapper.eq("catalog_id",catelogId);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){ //如果进行了品牌id查询
            queryWrapper.eq("brand_id",brandId);
        }
        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){ //如果最低价格不为空
                queryWrapper.ge("price",min);
        }
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(max)){ //如果设置了最大价格查询
            try {
                BigDecimal maxPrice = new BigDecimal(max);
                if(maxPrice.compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){

            }

        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据spu查询所有的sku信息
     * @param spuId
     * @return
     */
    @Override
    public List<SkuInfoEntity> getAllSkuInfoBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    /**
     * 查询商品详情页的数据
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();

        //=========================使用JUC进行并发编程========================
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1.sku的基本信息  pms_sku_info
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        //销售属性的查询需要依赖于infoFuture的结果，并且没有返回结果，所以使用thenAcceptAsync
        CompletableFuture<Void> saleFuture = infoFuture.thenAcceptAsync(res -> {
            //3.spu的销售属性的sku分组信息
            List<SkuItemVo.SkuItemSaleAttrVo> saleAttr = saleAttrValueService.getSpuSaleAttrBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttr);
        }, executor);
        //介绍属性的查询需要依赖于infoFuture的结果，并且没有返回结果，所以使用thenAcceptAsync
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync(res -> {
            //4.spu的介绍  pms_spu_info_desc
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesp(spuInfoDescEntity);
        }, executor);
        //属性分组的查询需要依赖于infoFuture的结果，并且没有返回结果，所以使用thenAcceptAsync
        CompletableFuture<Void> groupFuture = infoFuture.thenAcceptAsync(res -> {
            //5.spu的属性分组信息
            List<SkuItemVo.SpuItemAttrGroupVo> groupAttrs = groupService.getAttrAndAttrGroupByCatalogIdAndSpuId(res.getCatalogId(), res.getSpuId());
            skuItemVo.setGroupAttrs(groupAttrs);
        }, executor);

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //2.sku的图片信息  pms_sku_images
            List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);

        //只有当所有的异步任务都完成后才可以结束
        try {
            CompletableFuture.allOf(saleFuture,descFuture,groupFuture,imageFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return skuItemVo;
    }


}