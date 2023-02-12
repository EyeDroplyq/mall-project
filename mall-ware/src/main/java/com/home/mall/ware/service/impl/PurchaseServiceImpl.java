package com.home.mall.ware.service.impl;

import com.home.common.constant.PurchaseDetailStatus;
import com.home.common.constant.PurchaseStatus;
import com.home.mall.ware.entity.PurchaseDetailEntity;
import com.home.mall.ware.service.PurchaseDetailService;
import com.home.mall.ware.service.WareSkuService;
import com.home.mall.ware.vo.Item;
import com.home.mall.ware.vo.MergeVo;
import com.home.mall.ware.vo.PurchaseDoneVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.ware.dao.PurchaseDao;
import com.home.mall.ware.entity.PurchaseEntity;
import com.home.mall.ware.service.PurchaseService;

import javax.annotation.Resource;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Resource
    private PurchaseDetailService purchaseDetailService;

    @Resource
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    //查询没有分配的采购单
    @Override
    public PageUtils queryUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );
        return new PageUtils(page);
    }

    /**
     * 合并采购单
     *
     * @param mergeVo
     */
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        //判断有没有采购单的id,如果没有采购单的id我们创建出一个采购单
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) { //没有采购单，快速创建一个采购单
            //创建一个采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            //初始化这个采购单的信息
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(PurchaseStatus.PurchaseEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        //如果有采购单id的话，将采购需求的采购单id更新为采购单的id
        List<Long> items = mergeVo.getItems(); //得到当前进行合并的采购需求的id
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setStatus(PurchaseDetailStatus.PurchaseDetailEnum.ASSIGN.getCode());
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        //合并后还需要将采购单的更新时间更改
        PurchaseEntity purchaseEntity = this.getById(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
    }

    /**
     * 领取采购单
     * @param purchaseIds
     */
    @Override
    public void receivePurchase(List<Long> purchaseIds) {

        List<PurchaseEntity> purchaseEntities = purchaseIds.stream().map(item -> {
            //修改采购单状态和采购单的更新时间
            //根据采购单id集合查询出所有的采购单
            PurchaseEntity purchaseEntity = this.getById(item);
            return purchaseEntity;
        }).filter(item -> {
            //必须过滤出状态是新建或者是已分配的采购单
            if(item.getStatus() == PurchaseStatus.PurchaseEnum.CREATED.getCode() || item.getStatus() == PurchaseStatus.PurchaseEnum.ASSIGN.getCode()){
                return true;
            }
            return false;
        }).map(item->{
            item.setUpdateTime(new Date());
            item.setStatus(PurchaseStatus.PurchaseEnum.RECEIVED.getCode());
            return item;
        }).collect(Collectors.toList());
        this.updateBatchById(purchaseEntities);

        //更新采购需求单的信息，主要是将采购单对应的采购需求单中的状态修改为正在采购
        purchaseEntities.forEach(item->{
            //根据采购单id查询对应的采购需求单
            QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("purchase_id",item.getId());
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.list(queryWrapper);
            purchaseDetailService.updateReceivePurchaseDetailInfo(purchaseDetailEntities);
        });
    }

    /**
     * 完成采购方法
     * @param purchaseDoneVo
     */
    @Override
    public void purchaseDone(PurchaseDoneVo purchaseDoneVo) {
        Boolean flag= true; //表示采购需求是否正常完成
        Long purchaseId = purchaseDoneVo.getId();//采购单id
        //1.更新采购需求的信息
        List<Item> items = purchaseDoneVo.getItems(); //items中有每个采购需求的id、status
        List<PurchaseDetailEntity> updates=new ArrayList<>();
        for (Item item : items) {
            //查询对应的采购需求
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if(item.getStatus()==PurchaseDetailStatus.PurchaseDetailEnum.HASERROR.getCode()){ //如果采购需求采购失败
                flag=false;
                //更新采购需求的状态
                purchaseDetailEntity.setStatus(item.getStatus());
            }else{ //如果采购完成了
                //更新采购需求的状态
                purchaseDetailEntity.setStatus(item.getStatus());
                //3.更新库存信息
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.updateStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
//            purchaseDetailService.updateById(purchaseDetailEntity);
            updates.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(updates);
        //2.更新采购单的信息
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        //采购单信息的更新，需要依靠采购需求的状态，如果采购需求中有异常，那么采购单就有异常，只有采购需求全部正常完成，采购单才能正常完成
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setStatus(flag?PurchaseStatus.PurchaseEnum.FINISHED.getCode():PurchaseStatus.PurchaseEnum.HASERROR.getCode());
        this.updateById(purchaseEntity);

    }


}