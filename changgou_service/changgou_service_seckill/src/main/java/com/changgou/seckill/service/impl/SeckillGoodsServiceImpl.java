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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void authoPush() {
        // 1. 获取到时间段集合
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {
            String startTime = DateFormatUtils.format(dateMenu, "yyyy-MM-dd HH:mm:ss");
            String endTime = DateFormatUtils.format(DateUtil.addDateHour(dateMenu, 2), "yyyy-MM-dd HH:mm:ss");
            String dateStr = DateFormatUtils.format(dateMenu,"yyyyMMddHH");
            //2.循环时间段,查询符合要求的秒杀商品

            //条件1, 审核通过的

            //条件2, 库存大于0的

            //条件3, 秒杀商品的开始时间>=时间段的开始时间 并且 秒杀商品的结束时间<时间段的开始时间+2小时
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status","1");
            criteria.andGreaterThan("stockCount","0");
            criteria.andGreaterThanOrEqualTo("startTime",startTime);
            criteria.andLessThan("endTime",endTime);

            Set keys = redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + dateStr).keys();
            if(keys!=null && keys.size()>0){
                criteria.andNotIn("id",keys);
            }

            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            if (seckillGoodsList==null || seckillGoodsList.size()==0){
                logger.info("{}--{}的时间段, 暂无可更新的秒杀商品数据", startTime,endTime);
                continue;
            }

            //3.将秒杀商品数据循环放入缓存中
            /*
            * Redis的缓存结构:
            * 缓存结构1 (HASH结构) :
            *   key: 前缀+时间菜单的字符串 (类似于2020301014)
            *   value:
            *       key: 秒杀商品的ID
            *       value: 秒杀商品对象
            *
            * 缓存结构2:(STRING结构) :
            *       key: 前缀+秒杀商品ID
            *       value: 秒杀商品的库存
            *
            *
            *
            * */


            for (SeckillGoods seckillGoods : seckillGoodsList) {
                //保存缓存结构1, 将商品保存
                redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + dateStr).put(seckillGoods.getId(), seckillGoods);

                //保存缓存结构2,将商品的剩余库存保存到STRING中
                redisTemplate.opsForValue().set(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + seckillGoods.getId(),seckillGoods.getStockCount());
            }


        }
    }

    @Override
    public List<SeckillGoods> list(String time) {
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps(Constants.SECKILL_GOODS_KEY + time).values();
        if (seckillGoodsList!=null && seckillGoodsList.size()>0){
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                //返回给页面的秒杀产品的数量,要以缓存结构中的数量为准
                String stockCount = (String) redisTemplate.opsForValue().get(Constants.SECKILL_GOODS_STOCK_COUNT_KEY + seckillGoods.getId());
                seckillGoods.setStockCount(Integer.valueOf(stockCount));
            }
        }
        return seckillGoodsList;
    }
}
