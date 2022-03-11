package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
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
     *
     * @param hoscode 医院编码
     */
    Hospital getByHoscode(String hoscode);

    /**
     * @param page            当前页码
     * @param limit           每页记录数
     * @param hospitalQueryVo 查询条件
     */
    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    /**
     * 修改医院状态
     *
     * @param id     医院id
     * @param status 医院状态
     */
    void updateStatus(String id, Integer status);

    /**
     * 医院详情
     *
     * @param id 医院id
     */
    Map<String, Object> show(String id);

    /**
     * 根据医院编号获取医院名称
     *
     * @param hoscode 医院编号
     */
    String getHospName(String hoscode);

    /**
     * 根据医院名称获取医院列表
     *
     * @param hosname 医院名称
     */
    List<Hospital> findByHosname(String hosname);

    /**
     * 医院预约挂号详情
     *
     * @param hoscode 医院code
     */
    Map<String, Object> item(String hoscode);
}