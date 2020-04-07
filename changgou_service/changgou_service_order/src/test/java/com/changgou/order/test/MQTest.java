package com.changgou.order.test;

import com.changgou.OrderApplication;
import com.changgou.config.RabbitMQConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = OrderApplication.class)
@RunWith(SpringRunner.class)
public class MQTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMsg(){
        rabbitTemplate.convertAndSend(RabbitMQConfig.RELAY_QUEUE, (Object)"123", new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("20000"); //发送消息对延迟队列中，这个消息的TTL存活时间
                return message;
            }
        });
    }
}
