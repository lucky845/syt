package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    /**
     * 查询医院信息
     *
     * @param hoscode 医院号
     */
    Hospital findByHoscode(String hoscode);

    /**
     * 根据医院code获取医院信息
     *
     * @param hoscode 医院编号
     */
    Hospital getHospitalByHoscode(String hoscode);
}