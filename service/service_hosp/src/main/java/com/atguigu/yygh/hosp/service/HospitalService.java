package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {

    /**
     * 上传医院信息
     *
     * @param paramMap 医院信息参数
     */
    void saveHospital(Map<String, Object> paramMap);

    /**
     * 查询医院
     * @param hoscode 医院编码
     */
    Hospital getByHoscode(String hoscode);

}