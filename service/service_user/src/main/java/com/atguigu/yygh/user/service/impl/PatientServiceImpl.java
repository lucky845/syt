package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author lucky845
 * @since 2022-03-14
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 获取就诊人列表
     *
     * @param userId 用户id
     */
    @Override
    public List<Patient> findAllByUserId(Long userId) {
        // 根据user_id查询所有就诊人信息列表
        QueryWrapper<Patient> patientQueryWrapper = new QueryWrapper<>();
        patientQueryWrapper.eq("user_id", userId);
        List<Patient> patientList = baseMapper.selectList(patientQueryWrapper);
        // 通过远程调用,得到编码具体内容,查询数据字典表
        // 封装其他参数
        patientList.forEach(this::packgePatient);
        return patientList;
    }

    /**
     * 根据id获取就诊人信息
     *
     * @param id 用户id
     */
    @Override
    public Patient getPatientById(Long id) {
        return this.packgePatient(baseMapper.selectById(id));
    }

    /**
     * 封装patient对象
     *
     * @param patient 就诊人对象
     */
    private Patient packgePatient(Patient patient) {
        // 联系人证件
        String certificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());
        // 省
        String provinceName = dictFeignClient.getName(patient.getProvinceCode());
        // 市
        String cityName = dictFeignClient.getName(patient.getCityCode());
        // 区
        String districtName = dictFeignClient.getName(patient.getDistrictCode());

        // 填充参数
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("provinceString", provinceName);
        patient.getParam().put("cityString", cityName);
        patient.getParam().put("districtString", districtName);
        patient.getParam().put("fullAddress", provinceName + cityName + districtName + patient.getAddress());
        return patient;
    }

}
