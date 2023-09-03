package com.dazhou.dzojbackendjudgeservice.rabbitmq;

import com.dazhou.dzojbackendjudgeservice.judge.JudgeService;
import com.rabbitmq.client.Channel;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author da zhou
 */
@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId = Long.parseLong(message);
        judgeService.doJudge(questionSubmitId);
/*        try {

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
           deliveryTag:deliveryTag（唯一标识 ID）它代表了 RabbitMQ 向该 Channel 投递的这条消息的唯一标识 ID,递增
            multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
            requeue：被拒绝的是否重新入队列
            channel.basicNack(deliveryTag, false, false);
        }*/
    }

}