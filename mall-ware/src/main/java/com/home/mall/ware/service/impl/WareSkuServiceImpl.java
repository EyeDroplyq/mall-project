package com.home.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.home.common.to.HasStockTo;
import com.home.common.to.OrderTo;
import com.home.common.to.StockTo;
import com.home.common.to.WareOrderDetailTo;
import com.home.common.utils.R;
import com.home.mall.ware.constant.OrderStatusEnum;
import com.home.mall.ware.entity.WareOrderTaskDetailEntity;
import com.home.mall.ware.entity.WareOrderTaskEntity;
import com.home.mall.ware.exception.WareException;
import com.home.mall.ware.feign.OrderFeignService;
import com.home.mall.ware.feign.ProductSkuInfoService;
import com.home.mall.ware.service.WareOrderTaskDetailService;
import com.home.mall.ware.service.WareOrderTaskService;
import com.home.mall.ware.vo.LockStockResult;
import com.home.mall.ware.vo.OrderItemVo;
import com.home.mall.ware.vo.OrderVo;
import com.home.mall.ware.vo.WareSkuLock;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Resource
    ProductSkuInfoService productSkuInfoService;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

//    @RabbitHandler
//    public void unlockStock(StockTo stockTo, Message message, Channel channel) throws IOException {
//        WareOrderDetailTo detailTo = stockTo.getWareOrderDetailTo();
//        Long id = detailTo.getId();
//        //当我们把消息发到消息队列中的时候会有两种情况，第一种情况是锁定库存成功了，但是后面的业务失败了导致这个库存需要进行解库存
//        //第二种情况就是我们锁库存的时候就失败了，但是前面已经锁的会给我们发消息，但是我们的数据库中的锁库存表中其实是没有这个信息的，所以这个情况不需要进行一个解锁
//        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(id);
//        if (byId != null) {
//            //需要进行解库存
//            //进行解库存之前还要判断是不是订单被取消了，只有这种情况才会进行解库存
//            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(byId.getTaskId());
//            R r = orderFeignService.getOrderStatus(wareOrderTaskEntity.getOrderSn());
//            if (r.getCode() == 0) {
//                //如果远程调用成功了
//                OrderVo data = r.getData("data", new TypeReference<OrderVo>() {
//                });
//                if (data != null) {
//                    if (data.getStatus().equals(OrderStatusEnum.CANCLED)) {
//                        unlock(byId.getSkuId(),byId.getWareId(),byId.getSkuNum(),id);
//                        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//                    }else{
//                        //不进行解库存
//                        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//                    }
//                }
//            } else {
//                //如果远程调用失败了
//                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
//            }
//        } else {
//            //锁库存的时候发生失败，这种情况不需要进行解库存
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//        }
//    }

    private void unlock(Long skuId, Long wareId, Integer num, Long detailId) {
        this.baseMapper.unlock(skuId, wareId, num);
        //更新锁库存详情单中的锁库存状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(detailId);
        entity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(entity);
    }

    /**
     * 采购完成时更新库存信息
     *
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    @Override
    public void updateStock(Long skuId, Long wareId, Integer skuNum) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId).eq("ware_id", wareId);
        List<WareSkuEntity> wareSkuEntities = this.list(queryWrapper);
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) { //如果库存信息中没有这个库存的话，就是保存操作
            //保存操作
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            try {
                //远程调用，保存skuname信息
                R info = productSkuInfoService.info(skuId);
                Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    String skuName = (String) skuInfo.get("skuName");
                    wareSkuEntity.setSkuName(skuName);
                }
            } catch (Exception e) {

            }
            this.save(wareSkuEntity);
        } else {
            //否则为更新操作
            this.baseMapper.updateStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 查询对应的sku是否有库存，返回的结果是 skuid,hasStock
     *
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


    @Override
    @Transactional(rollbackFor = WareException.class)
    public Boolean lockOrder(WareSkuLock vo) {
        //在锁库存之前创建一个锁库存工作单
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);
        //锁定库存
        List<OrderItemVo> locks = vo.getLocks();//得到所有要锁的属性
        List<SkuWareHasStock> skuWareHasStocks = locks.stream().map(item -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            skuWareHasStock.setSkuId(item.getSkuId());
            skuWareHasStock.setNum(item.getCount());
            List<Long> wareIds = this.baseMapper.getHasStockWareIds(item.getSkuId());
            skuWareHasStock.setWareId(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());
        //根据有库存的仓库信息去仓库锁定商品
        for (SkuWareHasStock skuWareHasStock : skuWareHasStocks) {
            Boolean skuStocked = false;//用来表示每个商品是不是锁住了
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareId = skuWareHasStock.getWareId();
            if (wareId.size() <= 0 || wareId.isEmpty()) {
                //没有有库存的仓库
                throw new WareException(skuId);
            }
            for (Long wId : wareId) {
                //如果有仓库有库存
                Long count = this.baseMapper.lockSkuStock(skuId, wId, skuWareHasStock.getNum());
                if (count == 1L) {
                    //锁定库存成功了
                    skuStocked = true;
                    //如果锁定库存成功了，也要创建一个锁库存任务单的详情信息
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", skuWareHasStock.getNum(), wareOrderTaskEntity.getId(), wId, 1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    //给消息队列发送消息，但是不能只发送锁库存表的id，因为如果之前的商品锁库存成功了，但是后面的失败了，那么前面的也会回滚但是前面的却是应该成功的
                    StockTo stockTo = new StockTo();
                    stockTo.setWareOrderId(wareOrderTaskEntity.getId());
                    WareOrderDetailTo wareOrderDetailTo = new WareOrderDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, wareOrderDetailTo);
                    stockTo.setWareOrderDetailTo(wareOrderDetailTo);

                    //----->发送消息给rabbitmq
                    //String exchange, String routingKey, Object message
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockTo);
                    break;//只要是一个仓库锁住了，就可以进行下一个商品了
                } else {
                    //锁定库存失败了，重试下一个仓库
                }
            }
            if (skuStocked == false) {
                //所有所有仓库都尝试了但是还是没有锁住
                throw new WareException(skuId);
            }
        }
        return true;
    }

    @Override
    public void unlockStock(StockTo stockTo) {
        WareOrderDetailTo detailTo = stockTo.getWareOrderDetailTo();
        Long id = detailTo.getId();
        //当我们把消息发到消息队列中的时候会有两种情况，第一种情况是锁定库存成功了，但是后面的业务失败了导致这个库存需要进行解库存
        //第二种情况就是我们锁库存的时候就失败了，但是前面已经锁的会给我们发消息，但是我们的数据库中的锁库存表中其实是没有这个信息的，所以这个情况不需要进行一个解锁
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(id);
        if (byId != null) {
            //需要进行解库存
            //进行解库存之前还要判断是不是订单被取消了，只有这种情况才会进行解库存

            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(stockTo.getWareOrderId());
            R r = orderFeignService.getOrderStatus(wareOrderTaskEntity.getOrderSn());
            if (r.getCode() == 0) {
                //如果远程调用成功了
                OrderVo data = r.getData( new TypeReference<OrderVo>() {
                });
                if (data == null || data.getStatus() == OrderStatusEnum.CANCLED.getCode()) {
                    if (byId.getLockStatus() == 1) {
                        //只有当锁库存详情单中的状态为已锁定的时候我们才会进行解库存，其他的两种状态都不会进行解库存
                        unlock(detailTo.getSkuId(), detailTo.getWareId(), detailTo.getSkuNum(), id);
                    }

                }
            } else {
                //如果远程调用失败了
                throw new RuntimeException("远程调用失败");
            }
        } else {
            //锁库存的时候发生失败，这种情况不需要进行解库存
        }
    }

    //处理释放订单之后的情况，当订单释放之后来进行解库存
    @Override
    @Transactional
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderSn));
        List<WareOrderTaskDetailEntity> detailEntityList = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskEntity.getId()).eq("lock_status", 1));
        for (WareOrderTaskDetailEntity entity : detailEntityList) {
            //Long skuId, Long wareId, Integer num, Long detailId
            unlock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }
    }

    /**
     * 给我们的结果就是哪个skuId在哪些仓库中有库存
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SkuWareHasStock {
        private Long skuId;//skuId
        private List<Long> wareId;//有库存的所有仓库的id
        private Integer num;

    }

}