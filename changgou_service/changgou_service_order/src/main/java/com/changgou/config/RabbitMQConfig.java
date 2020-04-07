package com.changgou.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/****
 * @Author:lx
 * @Description:
 * @Date 2019/7/31 10:50
 *****/
@Configuration
public class RabbitMQConfig {


    //延迟消息
    public static final String RELAY_QUEUE = "relay_queue";
    //交换机名称
    public static final String CANCEL_ORDER_PAY_EXCHANGE = "cancel_order_pay_exchange";

    //队列名称
    public static final String CANCEL_ORDER_QUEUE = "cancel_order_queue";

    //延迟消息
    @Bean
    public Queue relayQueue(){
        return QueueBuilder.durable(RELAY_QUEUE)
                .withArgument("x-dead-letter-exchange",CANCEL_ORDER_PAY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key","")
                .build();
    }



    @Bean
    public Queue cancelOrderQueue(){
        return new Queue(CANCEL_ORDER_QUEUE);
    }
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(CANCEL_ORDER_PAY_EXCHANGE);
    }
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(cancelOrderQueue()).to(directExchange()).with("");
    }


}