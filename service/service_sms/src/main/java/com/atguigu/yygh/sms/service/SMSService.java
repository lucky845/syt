package com.atguigu.yygh.sms.service;

import com.atguigu.yygh.vo.sms.SmsVo;

import java.util.Map;

public interface SMSService {

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param param 参数
     */
    boolean send(String phone, Map<String, Object> param);

    /**
     * 就诊人发送短信
     *
     * @param smsVo 短信对象
     */
    void sendMsg(SmsVo smsVo);
}
