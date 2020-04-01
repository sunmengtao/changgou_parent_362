package com.changgou.order.service;

public interface CartService {

    /**
     * 添加购物车
     * @param username 用户名
     * @param skuId 商品ID
     * @param num  商品数量（可以为正数和负数）
     */
    void add(String username,String skuId,Integer num);
}
