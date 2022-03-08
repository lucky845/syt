package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    /**
     * 上传医院信息
     *
     * @param paramMap 医院信息参数
     */
    @Override
    public void saveHospital(Map<String, Object> paramMap) {

        Hospital paramHospital = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Hospital.class);

        String hoscode = (String) paramMap.get("hoscode");
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        if (hospital != null) {
            // 更新操作
            paramHospital.setCreateTime(hospital.getCreateTime());
            paramHospital.setUpdateTime(new Date());
            paramHospital.setIsDeleted(hospital.getIsDeleted());
            paramHospital.setStatus(hospital.getStatus());

            paramHospital.setId(hospital.getId());
            hospitalRepository.save(paramHospital);
        } else {
            // 添加操作
            paramHospital.setCreateTime(new Date());
            paramHospital.setUpdateTime(new Date());
            paramHospital.setIsDeleted(0);
            paramHospital.setStatus(0);
            hospitalRepository.save(paramHospital);
        }
    }

    /**
     * 查询医院
     *
     * @param hoscode 医院编码
     */
    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }
}