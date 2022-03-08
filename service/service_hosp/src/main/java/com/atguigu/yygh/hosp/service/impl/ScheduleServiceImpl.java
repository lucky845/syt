package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * 上传排班信息
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);
        Schedule mongoSchedule = scheduleRepository.findByHoscodeAndDepcodeAndHosScheduleId(schedule.getHoscode(), schedule.getDepcode(), schedule.getHosScheduleId());
        if (mongoSchedule == null) {
            // 添加
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        } else {
            // 更新
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(mongoSchedule.getIsDeleted());
            schedule.setId(mongoSchedule.getId());
            scheduleRepository.save(schedule);
        }
    }

    /**
     * 查询排班分页信息
     */
    @Override
    public Page<Schedule> getSchedulePage(Map<String, Object> paramMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);
        int page = Integer.parseInt((String) paramMap.get("page"));
        int limit = Integer.parseInt((String) paramMap.get("limit"));
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.Direction.ASC, "createTime");
        Example<Schedule> example = Example.of(schedule);
        return scheduleRepository.findAll(example, pageable);
    }

    /**
     * 删除排班
     *
     * @param hoscode       医院编号
     * @param hosScheduleId 排班id
     */
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule == null) {
            throw new YYGHException(20001, "没有对应的排班信息");
        } else {
            scheduleRepository.delete(schedule);
        }
    }
}