package com.changgou.entity;

/**
 * 常量
 * @author ZJ
 */
public class Constants {
    //购物车
    public static final String REDIS_CART = "cart_";

    //待支付订单key
    public final static String REDIS_ORDER_PAY = "order_pay_";
    //秒杀商品ke
    public static final String SECKILL_GOODS_KEY="seckill_goods_";
    //秒杀商品库存数key
    public static final String SECKILL_GOODS_STOCK_COUNT_KEY="seckill_goods_stock_count_";
    //秒杀用户key
    public static final String SECKILL_USER_KEY = "seckill_user_";

    /**
     * 商品上架交换器
     */
    public static final String GOODS_UP_EXCHANGE = "goods_up_exchange";


    //商品下架交换器
    public static final String GOODS_DOWN_EXCHANGE = "goods_down_exchange";
}
