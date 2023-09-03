package com.dazhou.dzojbackendquestionservice.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author da zhou
 */
@Component
@Slf4j
public class MyMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, String message) throws InterruptedException{
        // 1.全局唯一的消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 2.添加callback
        correlationData.getFuture().addCallback(
                result -> {
                    if(result.isAck()){
                        // 2.1.ack，消息成功
                        log.debug("消息发送成功, ID:{}", correlationData.getId());
                    }else{
                        // 2.2.nack，消息失败
                        log.error("消息发送失败, ID:{}, 原因{}",correlationData.getId(), result.getReason());
                    }
                },
                ex -> log.error("消息发送异常, ID:{}, 原因{}",correlationData.getId(),ex.getMessage())
        );
        rabbitTemplate.convertAndSend(exchange, routingKey, message,correlationData);
        // 休眠一会儿，等待ack回执
        Thread.sleep(2000);
    }

}