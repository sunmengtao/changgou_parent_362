package com.changgou.seckill.task;

import com.changgou.seckill.service.SeckillGoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日自动更新秒杀商品到缓存中的定时任务
 */
@Component
public class SeckillGoodsAutoPush {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 这里的CRON表达式临时设置为每分钟执行，方便测试。实际生产应该是每天的定点进行执行。
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void authPush(){
        logger.info("开始执行更新秒杀商品缓存的任务。。。。。");
        seckillGoodsService.authoPush();
        logger.info("执行更新秒杀商品缓存的任务结束。。。。。");
    }
}
