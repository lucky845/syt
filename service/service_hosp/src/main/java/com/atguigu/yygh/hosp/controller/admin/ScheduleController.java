package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author lucky845
 * @date 2022年03月09日 13:08
 */
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据医院编号 和 科室编号 ，查询排班规则数据
     *
     * @param page    当前页数
     * @param limit   每页记录数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    @ApiOperation(value = "查询排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public R getScheduleRule(@PathVariable long page,
                             @PathVariable long limit,
                             @PathVariable String hoscode,
                             @PathVariable String depcode) {
        Map<String, Object> map = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
        return R.ok().data(map);
    }

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @param workDate 工作日
     */
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public R getScheduleDetail( @PathVariable String hoscode,
                                @PathVariable String depcode,
                                @PathVariable String workDate) {
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode,depcode,workDate);
        return R.ok().data("list",list);
    }

}