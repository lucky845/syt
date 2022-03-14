package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utlis.AuthContextHolder;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author lucky845
 * @since 2022-03-14
 */
@Api(tags = "就诊人管理接口")
@RestController
@RequestMapping("/api/userinfo/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * 获取就诊人列表
     */
    @ApiOperation("获取就诊人列表")
    @GetMapping("/auth/findAll")
    public R findAll(HttpServletRequest request) {
        // 获取当前登陆用户id
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list = patientService.findAllByUserId(userId);
        return R.ok().data("list", list);
    }

    /**
     * 添加就诊人
     *
     * @param patient 就诊人信息
     */
    @ApiOperation("添加就诊人")
    @PostMapping("/auth/save")
    public R savePatient(@RequestBody Patient patient, HttpServletRequest request) {
        // 获取当前用户id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }

    /**
     * 根据id获取就诊人信息
     *
     * @param id 用户i
     */
    @ApiOperation("根据id获取就诊人信息")
    @GetMapping("/auth/get/{id}")
    public R getPatient(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return R.ok().data("patient", patient);
    }

    /**
     * 修改就诊人信息
     *
     * @param patient 就诊人信息
     */
    @ApiOperation("修改就诊人信息")
    @PutMapping("/auth/update")
    public R updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return R.ok();
    }

    /**
     * 删除就诊人信息
     *
     * @param id 用户id
     */
    @ApiOperation("删除就诊人")
    @DeleteMapping("/auth/remove/{id}")
    public R deletePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return R.ok();
    }

}

