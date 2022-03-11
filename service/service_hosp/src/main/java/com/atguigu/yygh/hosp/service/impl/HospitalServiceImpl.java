package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    /**
     * 注入远程调用数据字典
     */
    @Autowired
    private DictFeignClient dictFeignClient;

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

    /**
     * @param page            当前页码
     * @param limit           每页记录数
     * @param hospitalQueryVo 查询条件
     */
    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching()
                // 改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                // 改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true);

        Example<Hospital> example = Example.of(hospital, matcher);
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        // 封装医院等级数据
        pages.getContent().parallelStream().forEach(this::packgeHospital);

        return pages;
    }

    /**
     * 封装数据
     */
    private Hospital packgeHospital(Hospital hospital) {
        String hostypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), hospital.getHostype());
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;
    }

    /**
     * 修改医院状态
     *
     * @param id     医院id
     * @param status 医院状态
     */
    @Override
    public void updateStatus(String id, Integer status) {
        if (status.intValue() == 0 || status.intValue() == 1) {
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    /**
     * 医院详情
     *
     * @param id 医院id
     */
    @Override
    public Map<String, Object> show(String id) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = this.packgeHospital(hospitalRepository.findById(id).get());
        // 医院基本信息(包含医院等级)
        result.put("hospital", hospital);
        // 单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
        // 不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    /**
     * 根据医院编号获取医院名称
     *
     * @param hoscode 医院编号
     */
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (null != hospital) {
            return hospital.getHosname();
        }
        return "";
    }

    /**
     * 根据医院名称获取医院列表
     *
     * @param hosname 医院名称
     */
    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    /**
     * 医院预约挂号详情
     *
     * @param hoscode 医院code
     */
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        // 医院详情
        Hospital hospital = this.packgeHospital(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        // 预约规则
        result.put("bookingRule", hospital.getBookingRule());
        // 不需要重新返回
        hospital.setBookingRule(null);
        return result;
    }
}