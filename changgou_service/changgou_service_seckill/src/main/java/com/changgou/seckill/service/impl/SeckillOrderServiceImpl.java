package com.changgou.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Constants;
import com.changgou.seckill.config.CustomMessageSender;
import com.changgou.seckill.config.RabbitMQConfig;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.util.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CustomMessageSender customMessageSender;

    @Override
    public boolean add(String username, String time, Long id) {

        //1.流量攻击屏蔽 ( 同一个用户针对同一个订单,瞬间或者极短时间内连续 ( 并发 ) 下单)
        //使用分布式锁的方式进行控制,由于incr在redis中是原子性自增那么从一个默认值开始第一次自增时,如果出现多线程同时对
        //同一个key进行自增,只有一个线程能将结果自增为1,其他线程自增后都是1+
        Long increment = redisTemplate.opsForValue().increment(Constants.SECKILL_USER_KEY + username + "_" + id);
        if (increment!=1){
            logger.error("出现频繁下单流量,拒绝!");
            return false;
        }
        redisTemplate.expire(Constants.SECKILL_GOODS_KEY + username + "_" + id, 5, TimeUnit.SECONDS);

        //针对同一个商品,用户多次下单,应该屏蔽
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(username);
        seckillOrder.setSeckillId(id);

        int count = seckillOrderMapper.selectCount(seckillOrder);
        if (count>0){
            logger.error("不能多次下单! username: {}", username,id);
            return false;
        }

        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + time).get(id);
        if (seckillGoods==null){
            logger.error("商品不存在! username:{}, id:{}", username, id);
            return false;
        }


        //4.根据秒杀商品ID从缓存中查找剩余库存数量,并判断
        String stockCount = (String) redisTemplate.opsForValue().get(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + id);
        if (stockCount==null || Integer.valueOf(stockCount)<=0){
            logger.error("商品售停! username:{},id:{}", username,id);
            return false;
        }

        //5.减少秒杀商品对应的库存数量
        Long decrement = redisTemplate.opsForValue().decrement(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + id);
        if (decrement<0){
            redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + time).delete(id);
            redisTemplate.delete(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + id);
            logger.error("商品数据超卖! username: {} , id:{}", username, id);
            return false;
        }

        //6.将当前下单的订单信息,存入MQ
        SeckillOrder seckillOrderDB = new SeckillOrder();
        seckillOrderDB.setId(idWorker.nextId());
        seckillOrderDB.setUserId(username);
        seckillOrderDB.setSellerId(seckillGoods.getSellerId());
        seckillOrderDB.setStatus("0");
        seckillOrderDB.setMoney(seckillGoods.getCostPrice());

        customMessageSender.sendMessage("", RabbitMQConfig.SECKILL_ORDER, JSON.toJSONString(seckillOrderDB));

        return true;
    }
}
