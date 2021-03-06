package com.atguigu.yygh.sms.service.impl;

import com.atguigu.yygh.sms.service.SMSService;
import com.atguigu.yygh.sms.utils.HttpUtils;
import com.atguigu.yygh.vo.sms.SmsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucky845
 * @date 2022年03月11日 21:11
 */
@Slf4j
@Service
public class SMSServiceImpl implements SMSService {

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param param 参数
     */
    @Override
    public boolean send(String phone, Map<String, Object> param) {
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "你的appcode";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("param", "code:" + param.get("code"));
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            return true;
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 就诊人发送短信
     *
     * @param smsVo 短信对象
     */
    @Override
    public void sendMsg(SmsVo smsVo) {

        // 测试使用,并未正式发送
        // log.info("就诊人发送短信: {}", smsVo.getPhone() + "---" + smsVo);

        System.out.println("smsVo = " + smsVo);
    }
}
