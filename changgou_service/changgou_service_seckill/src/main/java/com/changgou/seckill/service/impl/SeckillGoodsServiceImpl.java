package com.changgou.seckill.service.impl;

import com.changgou.entity.Constants;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.service.SeckillGoodsService;
import com.changgou.util.DateUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void authoPush() {
        //1.获取到时间段集合（12个时间段的开始时间点集合）
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {
            String startTime = DateFormatUtils.format(dateMenu, "yyyy-MM-dd HH:mm:ss");
            String endTime = DateFormatUtils.format(DateUtil.addDateHour(dateMenu, 2), "yyyy-MM-dd HH:mm:ss");
            String dateStr =  DateFormatUtils.format(dateMenu, "yyyyMMddHH");
            //2.循环时间段，查询符合要求的秒杀商品

            //条件1：审核通过的
            //条件2：库存大于0的
            //条件3：秒杀商品的开始时间>=时间段的开始时间  并且  秒杀商品的结束时间<时间段的开始时间+2小时
            //条件4：已经保存入缓存中的商品ID，应该排除在外。
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", "1");//审核通过的
            criteria.andGreaterThan("stockCount", 0);//剩余库存数
            criteria.andGreaterThanOrEqualTo("startTime", startTime);//开始时间
            criteria.andLessThan("endTime", endTime);//结束时间

            Set keys = redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + dateStr).keys();
            if(keys!=null&& keys.size()>0){
                criteria.andNotIn("id", keys);//ID范围不在商品ID集合范围内
            }

            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            if(seckillGoodsList==null || seckillGoodsList.size()==0){
                logger.info("{}-{}的时间段，暂无可更新的秒杀商品数据",startTime,endTime);
                continue;
            }


            //3.将秒杀商品数据循环放入缓存中
            /**
             * Redis的缓存结构：
             * 缓存结构1（HASH结构）：
             *      key：前缀+时间菜单的字符串（类似于2020401014）
             *      value：
             *          key：秒杀商品的ID
             *          value：秒杀商品对象
             *
             * 缓存结构2（STRING结构）：
             *      key：前缀+秒杀商品ID
             *      value：秒杀商品的库存
             */
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                //保存缓存结构1，将商品保存到HASH中。目的在于秒杀首页能通过菜单时间来快速查询出HASH中所有秒杀商品。
                redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + dateStr).put(seckillGoods.getId(), seckillGoods);

                //保存缓存结构2，将商品的剩余库存保存到STRING中。 目的在秒杀下单的时候进行库存预扣减。
                redisTemplate.opsForValue().set(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + seckillGoods.getId(), seckillGoods.getStockCount());
            }

        }



    }


    @Override
    public List<SeckillGoods> list(String time) {
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + time).values();
        if(seckillGoodsList!=null && seckillGoodsList.size()>0){
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                //返回给页面的秒杀商品的库存数量，要以缓存结构二中的数量为准
                try {
                    String stockCount = (String)redisTemplate.opsForValue().get(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + seckillGoods.getId());
                    seckillGoods.setStockCount(Integer.valueOf(stockCount));
                } catch (NumberFormatException e) {
                    logger.error("操作商品的数量异常，seckillGoodsId:{}",seckillGoods.getId());
                    e.printStackTrace();
                }
            }
        }
        return seckillGoodsList;
    }
}
