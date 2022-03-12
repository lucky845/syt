package com.atguigu.yygh.sms.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.sms.service.SMSService;
import com.atguigu.yygh.sms.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lucky845
 * @date 2022年03月11日 21:10
 */
@RestController
@RequestMapping("/api/sms")
public class SMSController {

    @Autowired
    private SMSService smsService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    @GetMapping("/send/{phone}")
    public R code(@PathVariable String phone) {
        // 从redis获取验证码
        String code = (String) redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) return R.ok();

        code = RandomUtil.getFourBitRandom();
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        // boolean isSend = smsService.send(phone, param);
//        if (isSend) {
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return R.ok();
//        } else {
//            return R.error().message("发送短信失败");
//        }

    }

}
