package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 根据医院编号，查询医院所有科室列表
     *
     * @param hoscode 医院编号
     */
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        // 创建list集合,用于最终数据封装
        List<DepartmentVo> result = new ArrayList<>();

        // 根据医院编号,查询医院所有的科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> example = Example.of(departmentQuery);
        // 所有科室列表 departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        // 根据大科室编号, bigCode分组, 获取每个大科室下的子科室
        Map<String, List<Department>> departmentMap = departmentList.stream()
                .collect(Collectors.groupingBy(Department::getBigcode));
        // 遍历map集合, departmentMap
        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()) {
            // 大科室编号
            String bigCode = entry.getKey();
            // 大科室对应的全部数据
            List<Department> departmentDataList = entry.getValue();
            // 封装大科室
            DepartmentVo departmentvo = new DepartmentVo();
            // 封装大科室编号
            departmentvo.setDepcode(bigCode);
            // 封装大科室名
            departmentvo.setDepname(departmentDataList.get(0).getBigname());

            // 封装小科室
            ArrayList<DepartmentVo> childrenList = new ArrayList<>();
            for (Department department : departmentDataList) {
                DepartmentVo departmentChildren = new DepartmentVo();
                departmentChildren.setDepcode(department.getDepcode());
                departmentChildren.setDepname(department.getDepname());
                // 封装到list集合
                childrenList.add(departmentChildren);
            }
            // 把小科室list集合放到大科室的children属性里面
            departmentvo.setChildren(childrenList);
            // 放到最终list中
            result.add(departmentvo);
        }

        return result;
    }

    /**
     * 根据科室编号，和医院编号，查询科室名称
     *
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    @Override
    public Object getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            return department.getDepname();
        }
        return null;
    }
}