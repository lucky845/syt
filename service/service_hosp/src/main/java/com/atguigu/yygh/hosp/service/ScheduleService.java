package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {

    /**
     * 上传排班信息
     */
    void save(Map<String, Object> paramMap);

    /**
     * 查询排班分页信息
     */
    Page<Schedule> getSchedulePage(Map<String, Object> paramMap);

    /**
     * 删除排班
     */
    void remove(String hoscode, String hosScheduleId);

    /**
     * 根据医院编号 和 科室编号 ，查询排班规则数据
     *
     * @param page    当前页数
     * @param limit   每页记录数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     *
     * @param hoscode  医院编号
     * @param depcode  科室编号
     * @param workDate 工作日期
     */
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    /**
     * 获取可预约排班数据
     *
     * @param page    当前页数
     * @param limit   每页记录数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    /**
     * 获取排班详情
     *
     * @param id 科室id
     */
    Schedule getScheduleById(String id);

    /**
     * 根据排班id获取预约下单数据(service_order远程调用使用)
     *
     * @param scheduleId 排班id
     */
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    /**
     * 修改排班
     *
     * @param schedule 排班对象
     */
    void update(Schedule schedule);

}
