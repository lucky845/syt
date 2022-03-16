package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author lucky845
 * @since 2022-03-16
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 创建订单
     *
     * @param scheduleId 排班id
     * @param patientId  就诊人id
     */
    Long saveOrder(String scheduleId, Long patientId);
}
