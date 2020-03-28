package com.itheima.listener;

import com.itheima.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class CreatePageHtmlListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PageService pageService;

    @RabbitListener(queues = "page_create_queue")
    public void msgHandle(String spuId){
        logger.info("开始生成静态页面,spuId:{}",spuId);
        pageService.createPageHtml(spuId);
        logger.info("生成静态页面完成,spuId",spuId);
    }
}
