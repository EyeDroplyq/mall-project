package com.home.mall.ware.service.impl;

import com.home.common.constant.PurchaseDetailStatus;
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

import com.home.mall.ware.dao.PurchaseDetailDao;
import com.home.mall.ware.entity.PurchaseDetailEntity;
import com.home.mall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and(wrapper->{
                wrapper.eq("id",key).or().eq("purchase_id",key).or().eq("sku_id",key);
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 更新采购单领取后的采购需求对应的信息
     * @param purchaseDetailEntities
     */
    @Override
    public void updateReceivePurchaseDetailInfo(List<PurchaseDetailEntity> purchaseDetailEntities) {
        List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item.getId());
            purchaseDetailEntity.setStatus(PurchaseDetailStatus.PurchaseDetailEnum.BUYING.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        this.updateBatchById(collect);
    }

}