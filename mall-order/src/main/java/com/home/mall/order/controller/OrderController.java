package com.home.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.home.mall.order.entity.OrderEntity;
import com.home.mall.order.service.OrderService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;



/**
 * 订单
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 22:48:14
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @GetMapping("/getOrderStatus/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn){
        OrderEntity orderEntity=orderService.getOrderStaus(orderSn);
        return R.ok().setDate(orderEntity);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 查询用户的订单列表
     */
    @PostMapping("/listWith")
    public R listWith(@RequestBody Map<String, Object> params){
        PageUtils page = orderService.queryPageList(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
