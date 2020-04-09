package com.changgou.order.service;

import com.changgou.order.pojo.Order;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface OrderService {

    /***
     * 查询所有
     * @return
     */
    List<Order> findAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Order findById(String id);

    /***
     * 新增
     * @param order
     */
    void add(Order order);

    /***
     * 修改
     * @param order
     */
    void update(Order order);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Order> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Order> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Order> findPage(Map<String, Object> searchMap, int page, int size);


    /**
     * 订单结算页提交订单处理
     *
     * @param order
     * @return
     */
    boolean submit(Order order);


    /**
     * 根据微信支付回调数据更新订单数据
     *
     * @param orderId       畅购订单ID
     * @param transactionId 微信支付订单ID
     */
    void updateOrder(String orderId, String transactionId);


    /**
     * 关闭微信支付订单
     *
     * @param orderId
     */
    void closeOrder(String orderId);


    /**
     * 批量发货
     *
     * @param orderList
     */
    void batchSend(List<Order> orderList);


    /**
     * 确认收货
     *
     * @param operator 操作人
     * @param orderId 订单ID
     */
    void take(String operator, String orderId);


    /**
     * 自动收货
     */
    void autoTake();
}