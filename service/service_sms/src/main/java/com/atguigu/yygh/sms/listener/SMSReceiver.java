package com.atguigu.yygh.sms.listener;

import com.atguigu.yygh.mq.mqConst.MqConst;
import com.atguigu.yygh.sms.service.SMSService;
import com.atguigu.yygh.vo.sms.SmsVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSReceiver {
    @Autowired
    private SMSService smsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_SMS_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_SMS),
            key = {MqConst.ROUTING_SMS_ITEM}
    ))
    public void send(SmsVo smsVo, Message message, Channel channel) {
        smsService.sendMsg(smsVo);
    }
}