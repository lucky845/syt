package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.hosp.utils.MD5;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lucky845
 * @date 2022年03月07日 9:45
 */
@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 上传医院信息
     */
    @ApiOperation(value = "上传医院")
    @PostMapping("/saveHospital")
    public R saveHospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 签名校验
        // 1. 获取医院系统传过来的签名,签名进行MD5加密
        String sign = (String) paramMap.get("sign");
        // 2. 根据传递过来的医院编码, 查询数据库,查询签名
        String hoscode = (String) paramMap.get("hoscode");
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = hospitalSetService.getOne(queryWrapper);
        if (hospitalSet == null) {
            throw new YYGHException(20001, "没有这个医院信息");
        }
        String signKey = hospitalSet.getSignKey();
        // 3. 把数据库查出来的签名进行MD5加密
        String str = MD5.encrypt(signKey);
        // 4. 判断签名是否一致
        if (str != null && sign != null && str.equals(sign)) {
            //传输过程中“+”转换为了“ ”，因此我们要转换回来
            String logoData = (String) paramMap.get("logoData");
            logoData = logoData.replaceAll(" ", "+");
            paramMap.put("logoData", logoData);
            hospitalService.saveHospital(paramMap);
            return R.ok().code(200);
        } else {
            throw new YYGHException(20001, "校验失败");
        }
    }

    /**
     * 获取医院信息
     */
    @ApiOperation(value = "获取医院信息")
    @PostMapping("hospital/show")
    public Result<Hospital> hospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验
        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)) {
            throw new YYGHException(20001, "失败");
        }

        // todo 签名校验

        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

}
