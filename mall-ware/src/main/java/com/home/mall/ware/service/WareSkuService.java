package com.home.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.to.HasStockTo;
import com.home.common.to.OrderTo;
import com.home.common.to.StockTo;
import com.home.common.utils.PageUtils;
import com.home.mall.ware.entity.WareSkuEntity;
import com.home.mall.ware.vo.LockStockResult;
import com.home.mall.ware.vo.WareSkuLock;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 23:15:16
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateStock(Long skuId, Long wareId, Integer skuNum);

    List<HasStockTo> hasStock(List<Long> skuIds);

    //锁库存
    Boolean lockOrder(WareSkuLock vo);

    void unlockStock(StockTo stockTo);

    void unlockStock(OrderTo orderTo);
}

