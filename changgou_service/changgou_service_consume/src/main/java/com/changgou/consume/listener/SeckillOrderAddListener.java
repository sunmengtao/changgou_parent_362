package com.changgou.consume.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.consume.config.RabbitMQConfig;
import com.changgou.consume.service.SeckillOrderService;
import com.changgou.seckill.pojo.SeckillOrder;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SeckillOrderAddListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @RabbitListener(queues = RabbitMQConfig.SECKILL_ORDER)
    public void msgHandle(Message message, Channel channel){

        try {
            //设置预抓取数量的限制,为了保护消费者不宕机
            channel.basicQos(300);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String seckillOrderJSON = new String(message.getBody());
        SeckillOrder seckillOrder = JSON.parseObject(seckillOrderJSON, SeckillOrder.class);
        boolean updateResult = true;

        try {
            updateResult = seckillOrderService.add(seckillOrder) >0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (updateResult){
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
