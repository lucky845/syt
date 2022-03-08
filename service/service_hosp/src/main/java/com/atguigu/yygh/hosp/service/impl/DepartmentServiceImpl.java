package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 上传科室信息
     *
     * @param paramMap 科室信息
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Department.class);
        Department mongoDepartment = departmentRepository.findByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if (mongoDepartment == null) {
            // 添加
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(department.getIsDeleted());
            departmentRepository.save(department);
        } else {
            // 修改
            department.setCreateTime(mongoDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(department.getIsDeleted());
            department.setId(mongoDepartment.getId());
            departmentRepository.save(department);
        }

    }

    /**
     * 科室分页信息
     */
    @Override
    public Page<Department> getDepartmentPage(Map<String, Object> paramMap) {
        int page = Integer.parseInt((String) paramMap.get("page"));
        int limit = Integer.parseInt((String) paramMap.get("limit"));
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.Direction.ASC, "createTime");
        Page<Department> all = departmentRepository.findAll(pageable);
        return all;
    }

    /**
     * 删除科室
     *
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }
}