package com.changgou.search.listener;

import com.changgou.search.service.EsManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商品上架MQ消费者监听器：根据MQ中的spuId导入数据到ES中
 */
@Component
public class GoodsUpListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EsManagerService esManagerService;

    @RabbitListener(queues = "search_add_queue")
    public void msgHandle(String spuId){
        logger.info("根据SPUID：{}导入数据到ES，开始执行---->",spuId);
        esManagerService.importBySpuId(spuId);
        logger.info("<----根据SPUID：{}导入数据到ES，执行完毕",spuId);
    }
}
