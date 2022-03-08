package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

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
}
