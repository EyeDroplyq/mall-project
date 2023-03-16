package com.home.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.home.common.constant.ProductConstant;
import com.home.common.exception.WareException;
import com.home.common.to.OrderTo;
import com.home.common.utils.R;
import com.home.common.vo.MemberRespVo;
import com.home.mall.order.constant.OrderConstant;
import com.home.mall.order.constant.OrderStatusEnum;
import com.home.mall.order.entity.OrderItemEntity;
import com.home.mall.order.entity.PaymentInfoEntity;
import com.home.mall.order.feign.CartFeignService;
import com.home.mall.order.feign.MemberFeignService;
import com.home.mall.order.feign.ProductFeignService;
import com.home.mall.order.feign.WmsFeignService;
import com.home.mall.order.interceptor.LoginInterceptor;
import com.home.mall.order.service.OrderItemService;
import com.home.mall.order.service.PaymentInfoService;
import com.home.mall.order.to.OrderCreateTo;
import com.home.mall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.order.dao.OrderDao;
import com.home.mall.order.entity.OrderEntity;
import com.home.mall.order.service.OrderService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    //放了防止方法传参，可以使用ThreadLocal这是一个技巧，但是要注意及时释放threadLocal不然可能会导致内存泄漏
    private ThreadLocal<OrderSubmitVo> threadLocal = new ThreadLocal<OrderSubmitVo>();
    @Resource
    private MemberFeignService memberFeignService;

    @Resource
    private CartFeignService cartFeignService;
    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private WmsFeignService wmsFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo orderConfirm() {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginInterceptor.loginUser.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> addressTask = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.address(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, executor);
        CompletableFuture<Void> cartItemTask = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCartItems();
            confirmVo.setItems(items);
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> skuIds = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R hasStock = wmsFeignService.hasStock(skuIds);
            List<SkuStockVo> stocks = hasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (stocks != null) {
                Map<Long, Boolean> collect = stocks.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(collect);
            }

        }, executor);
        confirmVo.setIntegration(memberRespVo.getIntegration());
        //保证幂等性的令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        //保存到redis中
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_PREFIX_TOKEN + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        //返回给前端，用于前端保存到请求头中
        confirmVo.setOrderToken(token);
        try {
            CompletableFuture.allOf(addressTask, cartItemTask).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return confirmVo;
    }

    @Override
//    @GlobalTransactional
    @Transactional
    public OrderSubmitRespVo submitOrder(OrderSubmitVo vo) {

        OrderSubmitRespVo orderSubmitRespVo = new OrderSubmitRespVo();
        //获取用户的信息
        MemberRespVo memberRespVo = LoginInterceptor.loginUser.get();
        //获取传过来的orderToken
        String voOrderToken = vo.getOrderToken();
        //获取redis中的令牌，比较令牌以及删除令牌要保证原子性，所以我们要使用lua脚本
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        //redis执行脚本的api，其中泛型是返回值类型
        Long res = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_PREFIX_TOKEN + memberRespVo.getId()), voOrderToken);
        if (res == 0L) {
            //令牌没有验证通过
            orderSubmitRespVo.setCode(1);//令牌没有验证通过
            return orderSubmitRespVo;
        } else {
            //令牌验证成功
            //创建订单、验价
            OrderCreateTo order = orderCreate();

            //验价
            BigDecimal voPayPrice = vo.getPayPrice();
            BigDecimal payAmount = order.getOrder().getPayAmount();
            if (Math.abs((voPayPrice.subtract(payAmount)).doubleValue()) < 0.01) {
                //验价成功
                //TODO 保存订单、锁定库存等
                saveOrder(order);
                //创建订单成功之后给消息队列发送一个订单的数据
                //向rabbitmq中发送消息的时候其实最关键的问题就是保证数据的可靠性，别让数据丢失了
              try{
                  rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
              }catch (Exception e){
                  //TODO 如果有异常那就说明这个消息发送失败了，我们可以在数据库中创建一个发送的消息记录表，将这个消息记录到这个表中
                  //TODO 然后使用定时任务定期去这个表中将这个表中的消息取出来重新进行一个发送
              }
                //远程调用库存服务来锁定库存
                WareSkuLock wareSkuLock = new WareSkuLock();
                wareSkuLock.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setTitle(item.getSkuName());
                    orderItemVo.setCount(item.getSkuQuantity());
                    return orderItemVo;
                }).collect(Collectors.toList());
                wareSkuLock.setLocks(locks);
                R r = wmsFeignService.lockOrder(wareSkuLock);
                if (r.getCode() == 0) {
                    //锁定库存成功
                    //提交订单
                    orderSubmitRespVo.setOrder(order.getOrder());
                    orderSubmitRespVo.setCode(0);
                    return orderSubmitRespVo;
                } else {
                    String msg = (String) r.get("msg");
                    throw new WareException(msg);
                }
            } else {
                //验价失败
                orderSubmitRespVo.setCode(2);//验价失败
                return orderSubmitRespVo;
            }
        }
    }

    @Override
    public OrderEntity getOrderStaus(String orderSn) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_sn", orderSn);
        OrderEntity orderEntity = this.baseMapper.selectOne(queryWrapper);
        return orderEntity;
    }

    //释放订单
    @Override
    public void releaseOrder(OrderEntity order) {
        OrderEntity orderEntity = this.getById(order.getId());
        if (orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode() || orderEntity.getStatus() == OrderStatusEnum.CANCLED.getCode()) {
            OrderEntity entity = new OrderEntity();
            entity.setId(orderEntity.getId());
            entity.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(entity);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            //当我释放完订单之后，可能因为网络问题导致我们的订单没有解锁成功，那么解库存的服务到期之后会认为我们的订单是待支付状态，所以不会给我们解锁库存，这样
            //会导致我们的需要进行释放的订单对应的库存永远解不开,所以只要我们释放订单成功了我们都要去通知一个解库存，这样就可以避免库存解不了的问题，而且不会进行重复解库存，因为在解库存之前会进行一个状态的判断的
            rabbitTemplate.convertAndSend("order-event-exchange","order.release.other",orderTo);
        }
    }

    @Override
    public PayVo getPayInfoByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderEntity.getOrderSn());
        BigDecimal total = orderEntity.getPayAmount().setScale(2, RoundingMode.UP);//保留小数点后两位，如果两位后面还有的话就进位
        payVo.setTotal_amount(total.toString());
        payVo.setSubject(orderEntity.getReceiverName()+"的订单");
        payVo.setBody(orderEntity.getReceiverName()+"的订单");
        return payVo;
    }

    @Override
    public PageUtils queryPageList(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );
        List<OrderEntity> res = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> items = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(items);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(res);
        return new PageUtils(page);
    }

    @Override
    public String handlerOrder(PayAsyncVo vo) {
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
        paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
        paymentInfoEntity.setSubject(vo.getSubject());
        paymentInfoEntity.setTotalAmount(new BigDecimal(vo.getTotal_amount()));
        paymentInfoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
        paymentInfoService.save(paymentInfoEntity);
        //修改订单状态
        String tradeStatus = vo.getTrade_status();
        if(tradeStatus.equals("TRADE_SUCCESS")){
            this.baseMapper.updateOrderStatusByOrderSn(vo.getOut_trade_no(),OrderStatusEnum.PAYED.getCode());
            return "success";
        }
        if(tradeStatus.equals("TRADE_FINISHED")){
            this.baseMapper.updateOrderStatusByOrderSn(vo.getOut_trade_no(),OrderStatusEnum.RECIEVED.getCode());
            return "success";
        }
        return "error";
    }

    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        List<OrderItemEntity> orderItems = order.getOrderItems();
        this.save(orderEntity);
        for (OrderItemEntity orderItem : orderItems) {
            orderItemService.save(orderItem);
        }
    }

    private OrderCreateTo orderCreate() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        OrderSubmitVo orderSubmitVo = threadLocal.get();
        //构造订单数据
        OrderEntity orderEntity = buildOrder(orderSubmitVo);
        orderCreateTo.setOrder(orderEntity);
        String orderSn = orderEntity.getOrderSn();
        Long orderId = orderEntity.getId();
        //构造订单项数据
        List<OrderItemEntity> orderItems = buildOrderItems(orderSn,orderId);
        orderCreateTo.setOrderItems(orderItems);
        computer(orderEntity, orderItems);

        return orderCreateTo;
    }

    private void computer(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal integrationAmount = new BigDecimal("0.0");//积分抵扣金额
        BigDecimal couponAmount = new BigDecimal("0.0");//优惠券抵扣金额
        BigDecimal integration = new BigDecimal("0.0");//订单的积分
        BigDecimal growth = new BigDecimal("0.0");//订单的成长值
        for (OrderItemEntity orderItem : orderItems) {
            BigDecimal realAmount = orderItem.getRealAmount();
            total = total.add(realAmount);
            integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount());
            couponAmount = couponAmount.add(orderItem.getCouponAmount());
            integration = new BigDecimal(orderItem.getGiftIntegration().toString()).multiply(new BigDecimal(orderItem.getSkuQuantity().toString()));
            growth = new BigDecimal(orderItem.getGiftGrowth().toString()).multiply(new BigDecimal(orderItem.getSkuQuantity().toString()));
        }
        orderEntity.setTotalAmount(total); //设置订单的订单总额
        total = total.subtract(integrationAmount).subtract(couponAmount);
        orderEntity.setPayAmount(total);//设置订单的应付总额
        orderEntity.setIntegrationAmount(integrationAmount);//设置订单的积分抵扣金额
        orderEntity.setCouponAmount(couponAmount);//设置订单的优惠券抵扣金额
        orderEntity.setIntegration(integration.intValue());//设置订单的积分
        orderEntity.setGrowth(growth.intValue());//设置订单的成长值
        orderEntity.setDeleteStatus(0);//设置订单的删除状态
    }

    //构造订单项列表
    private List<OrderItemEntity> buildOrderItems(String orderSn,Long orderId) {
        List<OrderItemVo> cartItems = cartFeignService.getCartItems();
        if (cartItems != null && cartItems.size() > 0) {
            List<OrderItemEntity> orderItemEntities = cartItems.stream().map(item -> {
                OrderItemEntity orderItemEntity = buildOrderItem(item,orderSn,orderId);
                return orderItemEntity;
            }).collect(Collectors.toList());
            return orderItemEntities;
        }
        return null;
    }

    //构造一个订单项
    private OrderItemEntity buildOrderItem(OrderItemVo item,String orderSn,Long orderId) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setSkuId(item.getSkuId());
        orderItemEntity.setOrderSn(orderSn);
        orderItemEntity.setOrderId(orderId);
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuPic(item.getImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        orderItemEntity.setSkuQuantity(item.getCount());
        List<String> skuAttrs = item.getSkuAttr();
        String skuAttr = StringUtils.collectionToDelimitedString(skuAttrs, ";");//将一个集合通过指定的字符分隔成一个字符串
        orderItemEntity.setSkuAttrsVals(skuAttr);
        //远程调用商品服务，根据skuId查询spu信息
        SpuInfoRespVo spuInfoRespVo = productFeignService.getSpuInfo(item.getSkuId());
        orderItemEntity.setSpuId(spuInfoRespVo.getId());
        orderItemEntity.setSpuName(spuInfoRespVo.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoRespVo.getBrandId().toString());
        orderItemEntity.setCategoryId(spuInfoRespVo.getCatalogId());
        orderItemEntity.setGiftIntegration(spuInfoRespVo.getBuyBounds().intValue());
        orderItemEntity.setGiftGrowth(spuInfoRespVo.getGrowBounds().intValue());
        orderItemEntity.setPromotionAmount(new BigDecimal("0.0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0.0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0.0"));
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal count = new BigDecimal(orderItemEntity.getSkuQuantity().toString());
        BigDecimal price = orderItemEntity.getSkuPrice();
        total = count.multiply(price);
        total = total.subtract(orderItemEntity.getIntegrationAmount()).subtract(orderItemEntity.getPromotionAmount()).subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(total);//设置实际应付的金额
        return orderItemEntity;
    }

    private OrderEntity buildOrder(OrderSubmitVo orderSubmitVo) {
        MemberRespVo memberRespVo = LoginInterceptor.loginUser.get();
        //创建订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(memberRespVo.getId());
        //设置订单号,使用包米豆给我们提供的这个方法，可以根据时间和id生成一个唯一的订单号
        String orderSn = IdWorker.getTimeId();
        orderEntity.setOrderSn(orderSn);
//        Long addrId = orderSubmitVo.getAddrId();//从提交的数据中获取收货人地址id
        MemberReceiveAddressEntity receiveMember = memberFeignService.getReceiveMember(1L);


        orderEntity.setReceiverName(receiveMember.getName());//收货人的名字
        orderEntity.setReceiverPhone(receiveMember.getPhone());//收货人的手机号
        orderEntity.setReceiverPostCode(receiveMember.getPostCode());//收件人的邮编
        orderEntity.setReceiverProvince(receiveMember.getProvince());//收件人的省份
        orderEntity.setReceiverDetailAddress(receiveMember.getDetailAddress());//收件人详细信息
        orderEntity.setReceiverRegion(receiveMember.getRegion());//收件人区
        orderEntity.setReceiverCity(receiveMember.getCity());//收件人城市
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());//订单状态
        orderEntity.setAutoConfirmDay(7);//七天自动收货
        orderEntity.setFreightAmount(new BigDecimal("0.0"));
        return orderEntity;
    }


}