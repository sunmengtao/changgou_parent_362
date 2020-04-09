package com.changgou.order.task;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoTakeTask {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0/5 * * * * ?")
    public void autoTake(){
        rabbitTemplate.convertAndSend("","order_take","_");

    }
}
