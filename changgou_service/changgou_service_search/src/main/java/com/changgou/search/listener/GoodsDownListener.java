package com.changgou.search.listener;

import com.changgou.search.service.EsManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsDownListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EsManagerService esManagerService;

    @RabbitListener(queues = "search_add_queue")
    public void msgHandle(String spuId){
        logger.info("根据SPUID: {}从ES删除数据,开始执行---->",spuId);
        esManagerService.importBySpuId(spuId);
        logger.info("<----根据SPUID: {}从ES删除数据,执行完毕",spuId);
    }
}
