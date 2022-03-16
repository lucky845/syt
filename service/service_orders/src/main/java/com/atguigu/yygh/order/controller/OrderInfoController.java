package com.atguigu.yygh.order.controller;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.order.service.OrderInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author lucky845
 * @since 2022-03-16
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 创建订单
     *
     * @param scheduleId 排班编号
     * @param patientId  就诊人编号
     */
    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public R submitOrder(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable String scheduleId,

            @ApiParam(name = "patientId", value = "就诊人id", required = true)
            @PathVariable Long patientId) {

        Long orderId = orderInfoService.saveOrder(scheduleId, patientId);
        return R.ok().data("orderId", orderId);
    }


}

