package com.atguigu.yygh.mq.mqConst;

public class MqConst {
    /**
     * 预约下单
     */
    public static final String EXCHANGE_DIRECT_ORDER = "exchange.direct.order";
    public static final String ROUTING_ORDER = "order";
    /**
     * 队列
     */
    public static final String QUEUE_ORDER = "queue.order";

    /**
     * 短信
     */
    public static final String EXCHANGE_DIRECT_SMS = "exchange.direct.sms";
    public static final String ROUTING_SMS_ITEM = "sms.item";
    /**
     * 队列
     */
    public static final String QUEUE_SMS_ITEM = "queue.sms.item";
}