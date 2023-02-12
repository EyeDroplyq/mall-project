package com.home.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.home.common.utils.R;
import com.home.mall.cart.constant.UserKeyConstant;
import com.home.mall.cart.feign.ProductServiceFeign;
import com.home.mall.cart.interceptor.CartInterceptor;
import com.home.mall.cart.service.CartService;
import com.home.mall.cart.to.UserInfoTo;
import com.home.mall.cart.vo.CartItemInfoVo;
import com.home.mall.cart.vo.CartVo;
import com.home.mall.cart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: lyq
 * @createDate: 8/2/2023
 * @version: 1.0
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    private  final  String CART_PREFIX="mall:cart:";
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private ProductServiceFeign productServiceFeign;
    @Autowired
    private ThreadPoolExecutor executor;

    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public CartItemInfoVo addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //判断redis中有没有这个商品
        String res = (String) cartOps.get(skuId.toString());
        if(StringUtils.isEmpty(res)){
            //如果redis中没有这个商品说明这是一个新商品，添加到redis中
            CartItemInfoVo cartItemInfoVo = new CartItemInfoVo();
            //使用JUC进行异步编排任务，因为这里涉及到两个远程调用
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R info = productServiceFeign.info(skuId);
                if (info.getCode() == 0) {
                    SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    cartItemInfoVo.setImage(skuInfo.getSkuDefaultImg());
                    cartItemInfoVo.setPrice(skuInfo.getPrice());
                    cartItemInfoVo.setTitle(skuInfo.getSkuTitle());
                }
            }, executor);

            CompletableFuture<Void> getSaleAttrListTask = CompletableFuture.runAsync(() -> {
                //远程调用去查询属性的组合信息
                List<String> list = productServiceFeign.getProductAttributesList(skuId);
                cartItemInfoVo.setSkuAttr(list);
            }, executor);
            try {
                //要阻塞等待两个任务都完成，不然主线程太快会变成守护线程自动退出，得不到我们想要的结果
                CompletableFuture.allOf(getSkuInfoTask,getSaleAttrListTask).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            cartItemInfoVo.setSkuId(skuId);
            cartItemInfoVo.setCount(num);
            String result = JSON.toJSONString(cartItemInfoVo);
            cartOps.put(skuId.toString(),result); //保存到redis中
            return cartItemInfoVo;
        }else{
            //如果redis中已经有这个数据了，说明购物车中之前已经添加了这个商品，只需要修改数量即可
            CartItemInfoVo itemInfoVo = JSON.parseObject(res, CartItemInfoVo.class);
            itemInfoVo.setCount(itemInfoVo.getCount()+num);
            cartOps.put(skuId.toString(),JSON.toJSONString(itemInfoVo));
            return itemInfoVo;
        }


    }

    /**
     * 根据skuId去redis查询对应的数据返回
     * @param skuId
     * @return
     */
    @Override
    public CartItemInfoVo showAddToCartSuccess(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String str = (String) cartOps.get(skuId.toString());
        CartItemInfoVo infoVo = JSON.parseObject(str, CartItemInfoVo.class);
        return infoVo;
    }

    @Override
    public CartVo getCart() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        CartVo cartVo = new CartVo();
        if(userInfoTo.getUserId()!=null){
            //如果不是临时用户的话，得到的是用户购物车

            String cartKey= CART_PREFIX+userInfoTo.getUserId();
            //还需要合并临时用户的购物车
            List<CartItemInfoVo> vo = getCartItemVo(CART_PREFIX + userInfoTo.getUserKey());
            if(vo!=null && vo.size()>0){
                //如果还有临时购物车的话,合并，然后将临时购物车清空
                for (CartItemInfoVo cartItemInfoVo : vo) {
                    addToCart(cartItemInfoVo.getSkuId(),cartItemInfoVo.getCount());
                }
                //清除临时购物车
                clearCart(CART_PREFIX + userInfoTo.getUserKey());
            }
            List<CartItemInfoVo> items=getCartItemVo(cartKey);
            cartVo.setItems(items);
        }else {
            //如果是临时用户
            String cartKey =CART_PREFIX + userInfoTo.getUserKey();
            List<CartItemInfoVo> items = getCartItemVo(cartKey);
            cartVo.setItems(items);

        }
        return cartVo;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItemInfoVo cartItemVo=getCartItem(skuId);
        cartItemVo.setCheck(check==1?true:false);
        String res = JSON.toJSONString(cartItemVo);
        cartOps.put(skuId.toString(),res);
    }


    @Override
    public void countItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItemInfoVo cartItemVo=getCartItem(skuId);
        if(num>0){
            cartItemVo.setCount(num);
            String res = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(),res);
        }else{
            cartOps.delete(skuId.toString());
        }
    }

    @Override
    public void deleteCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    private CartItemInfoVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String str = (String) cartOps.get(skuId.toString());
        CartItemInfoVo itemInfoVo = JSON.parseObject(str, CartItemInfoVo.class);
        return itemInfoVo;
    }

    private List<CartItemInfoVo> getCartItemVo(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> res = hashOps.values();
        if(res!=null && res.size()>0){
            List<CartItemInfoVo> collect = res.stream().map((obj) -> {
                String str = (String) obj;
                CartItemInfoVo itemInfoVo = JSON.parseObject(str, CartItemInfoVo.class);
                return itemInfoVo;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey="";
        if(userInfoTo.getUserId()!=null){
            //不是临时用户
            cartKey=CART_PREFIX+userInfoTo.getUserId();
        }else{
            //临时用户
            cartKey=CART_PREFIX+userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);

        return hashOps;
    }
}
