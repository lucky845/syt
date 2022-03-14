package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务类
 * </p>
 *
 * @author lucky845
 * @since 2022-03-14
 */
public interface PatientService extends IService<Patient> {

    /**
     * 获取就诊人列表
     *
     * @param userId 用户id
     */
    List<Patient> findAllByUserId(Long userId);

    /**
     * 根据id获取就诊人信息
     *
     * @param id 用户id
     */
    Patient getPatientById(Long id);

}
