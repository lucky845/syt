package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

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

    /**
     * 根据医院编号 和 科室编号 ，查询排班规则数据
     *
     * @param page    当前页数
     * @param limit   每页记录数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        // 1. 根据医院编号和科室编号查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        // 2. 根据工作日 workDate 日期进行分组
        Aggregation agg = Aggregation.newAggregation(
                // 匹配条件
                Aggregation.match(criteria),
                // 分组字段
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        // 3. 统计号源数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                // 排序
                Aggregation.sort(Sort.Direction.ASC, "workDate"),
                // 4. 实现分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );

        // 调用方法最终执行
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        // 分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();

        // 把日期对应星期获取
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        // 设置最终数据, 进行返回
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        resultMap.put("total", total);

        // 获取医院名称
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        // 其他参数
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hospital.getHosname());
        resultMap.put("baseMap", baseMap);

        return resultMap;
    }

    /**
     * 根据日期获取周几数据
     *
     * @param dateTime 日期
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     *
     * @param hoscode  医院编号
     * @param depcode  科室编号
     * @param workDate 工作日期
     */
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        // 根据参数查询MongoDB
        List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        // 遍历list,设置医院名称、科室名称、日期对应星期
        scheduleList.forEach(this::packageSchedule);
        return scheduleList;
    }

    /**
     * 封装排班详情其他值 医院名称、科室名称、日期对应星期
     *
     * @param schedule 排班对象
     */
    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname",
                departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

}