package com.atguigu.yygh.sms.service;

import java.util.Map;

public interface SMSService {

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param param 参数
     */
    boolean send(String phone, Map<String, Object> param);

}
