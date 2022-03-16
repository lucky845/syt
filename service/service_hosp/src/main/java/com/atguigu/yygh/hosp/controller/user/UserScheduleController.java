package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author lucky845
 * @date 2022年03月15日 19:30
 */
@Api(tags = "预约挂号")
@RestController
@RequestMapping("/api/hosp/hospital")
public class UserScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 获取可预约排班数据
     *
     * @param page    当前页数
     * @param limit   每页记录数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public R getBookingSchedule(
            @ApiParam(value = "当前页数", required = true)
            @PathVariable Integer page,

            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Integer limit,

            @ApiParam(value = "医院编号", required = true)
            @PathVariable String hoscode,

            @ApiParam(value = "科室编号", required = true)
            @PathVariable String depcode) {

        Map<String, Object> map = scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode);
        return R.ok().data(map);
    }

    /**
     * 获取排版数据
     *
     * @param hoscode  医院编号
     * @param depcode  科室编号
     * @param workDate 工作日
     */
    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public R findScheduleList(
            @PathVariable String hoscode,
            @PathVariable String depcode,
            @PathVariable String workDate) {
        List<Schedule> scheduleList = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return R.ok().data("scheduleList", scheduleList);
    }

    /**
     * 获取排班详情
     *
     * @param id 科室id
     */
    @ApiOperation(value = "获取排班详情")
    @GetMapping("/getSchedule/{id}")
    public R getDetailSchedule(
            @ApiParam(value = "排班id", required = true)
            @PathVariable String id) {

        Schedule schedule = scheduleService.getScheduleById(id);
        return R.ok().data("schedule", schedule);
    }

    /**
     * 根据排班id获取预约下单数据(service_order远程调用使用)
     *
     * @param scheduleId 排班id
     */
    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("/inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId) {

        return scheduleService.getScheduleOrderVo(scheduleId);
    }

}
