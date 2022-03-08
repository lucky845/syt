package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {

    /**
     * 上传科室信息
     *
     * @param paramMap 科室信息
     */
    void save(Map<String, Object> paramMap);

    /**
     * 科室分页信息
     */
    Page<Department> getDepartmentPage(Map<String, Object> paramMap);

    /**
     * 删除科室
     *
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    void remove(String hoscode, String depcode);
}