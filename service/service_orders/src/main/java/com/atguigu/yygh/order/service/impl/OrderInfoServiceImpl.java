package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utlis.HttpRequestHelper;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.hosp.client.HospitalFeignClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.mq.mqConst.MqConst;
import com.atguigu.yygh.mq.service.RabbitService;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.sms.SmsVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author lucky845
 * @since 2022-03-16
 */
@Slf4j
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 创建订单
     *
     * @param scheduleId 排班id
     * @param patientId  就诊人id
     */
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {

        // 1. 根据排班id获取排班信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

        // 2. 根据就诊人id获取就诊人信息
        Patient patient = patientFeignClient.getPatient(patientId);

        // 3. 平台系统请求第三方医院系统, 确认是否能挂号
        // 3.1 如果医院返回失败,挂号失败
        // 使用map集合封装需要传过医院数据
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", scheduleOrderVo.getHoscode());
        paramMap.put("depcode", scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId", scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate", new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime());
        // 挂号费用
        paramMap.put("amount", scheduleOrderVo.getAmount());
        // 就诊人信息
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType", patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex", patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone", patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode", patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode", patient.getDistrictCode());
        paramMap.put("address", patient.getAddress());
        // 联系人
        paramMap.put("contactsName", patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        // String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", "");

        // 使用HttpClient发送请求，请求医院接口
        JSONObject result =
                HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");

        // 根据医院接口返回状态码判断 200 成功
        if (result != null && result.getInteger("code") == 200) {
            // 3.2 如果返回成功,得到返回其他数据
            JSONObject jsonObject = result.getJSONObject("data");
            // 预约记录唯一标识(医院预约记录主键)
            String hosRecordId = jsonObject.getString("hosRecordId");
            // 预约序号
            Integer number = jsonObject.getInteger("number");
            // 取号时间
            String fetchTime = jsonObject.getString("fetchTime");
            // 取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");

            // 4. 如果医院接口返回成功, 添加上面三部分数据到数据库order_info
            OrderInfo orderInfo = new OrderInfo();
            // 设置添加数据-排班数据
            BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
            // 设置添加数据-就诊人数据
            // 订单号
            String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setScheduleId(scheduleId);
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setPatientId(patientId);
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());

            // 设置添加数据-医院接口返回数据
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);

            // 调用方法添加
            baseMapper.insert(orderInfo);

            // 5. 根据医院返回数据, 更新排班数量
            // 排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            // 排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");

            // 发送mq信息更新号源和短信通知
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);

            // 短信提示
            // 预约日期
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            // 短信模板参数
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};

            SmsVo smsVo = new SmsVo();
            smsVo.setTemplateCode("${name},您预约的: ${title},开始时间是: ${reserveDate},结束时间是: ${quitTime},价格是: ${amount}");
            smsVo.setPhone(orderInfo.getPatientPhone());
            smsVo.setParam(param);

            orderMqVo.setSmsVo(smsVo);

            // 发送消息
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);

            // 返回订单号
            return orderInfo.getId();
        } else {
            log.warn("预约挂号失败");
            throw new YYGHException(20001, "挂号失败");
        }
    }

}
