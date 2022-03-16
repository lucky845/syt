package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
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

    /**
     * 根据医院编号，查询医院所有科室列表
     *
     * @param hoscode 医院编号
     */
    List<DepartmentVo> findDeptTree(String hoscode);

    /**
     * 根据科室编号，和医院编号，查询科室名称
     *
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    Object getDepName(String hoscode, String depcode);

    /**
     * 根据医院编号和科室编号获取医院信息
     *
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    Department getDepartment(String hoscode, String depcode);
}