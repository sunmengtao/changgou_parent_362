package com.changgou.order.listener;

import com.changgou.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * 执行自动收货的MQ监听器
 * */
@Component
public class OrderAutoTakeListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "order_take")
    public void msgHandle(String msg){
        logger.info("开始执行自动收货了.....");
        orderService.autoTake();
    }
}
