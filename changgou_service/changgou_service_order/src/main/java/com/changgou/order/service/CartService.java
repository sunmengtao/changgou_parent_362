package com.changgou.order.service;

import java.util.Map;

public interface CartService {

    /**
     * 添加购物车
     * @param username 用户名
     * @param skuId 商品ID
     * @param num  商品数量（可以为正数和负数）
     */
    void add(String username,String skuId,Integer num);

    /**
     * 获取登录用户的购物车数据列表
     * @param username 用户名
     * @return
     */
    Map list(String username);


    /**
     * 勾选或取消购物车商品
     * @param username 用户名
     * @param skuId 商品ID
     * @param checked true表示勾选，false表示取消
     */
    void updateChecked(String username, String skuId, Boolean checked);

    /**
     * 从购物车中删除商品条目
     * @param skuId 商品ID
     */
    void delete(String username, String skuId);
}
