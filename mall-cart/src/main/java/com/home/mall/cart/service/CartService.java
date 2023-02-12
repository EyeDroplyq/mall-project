package com.home.mall.cart.service;

import com.home.mall.cart.vo.CartItemInfoVo;
import com.home.mall.cart.vo.CartVo;

/**
 * @description:
 * @author: lyq
 * @createDate: 8/2/2023
 * @version: 1.0
 */
public interface CartService {
    CartItemInfoVo addToCart(Long skuId, Integer num);

    CartItemInfoVo showAddToCartSuccess(Long skuId);

    /**
     * 获取整个购物车信息
     * @return
     */
    CartVo getCart();

    /**
     * 清空购物车
     * @param cartKey
     */
    void clearCart(String cartKey);

    /**
     * 修改选中状态
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 修改购物车中的商品数量信息
     * @param skuId
     * @param num
     */
    void countItem(Long skuId, Integer num);

    void deleteCartItem(Long skuId);
}
