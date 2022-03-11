package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author lucky845
 * @date 2022年03月09日 19:14
 */
@Api(tags = "医院显示接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospitalApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取分页列表
     *
     * @param page            当前页码
     * @param limit           每页记录数
     * @param hospitalQueryVo 查询对象
     */
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public R index(
            @PathVariable Integer page,
            @PathVariable Integer limit,
            HospitalQueryVo hospitalQueryVo) {

        Page<Hospital> pageModel = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return R.ok().data("pages", pageModel);
    }

    /**
     * 根据医院名称获取医院列表
     *
     * @param hosname 医院名称
     */
    @ApiOperation(value = "根据医院名称获取医院列表")
    @GetMapping("findByHosname/{hosname}")
    public R findByHosname(
            @ApiParam(name = "hosname", value = "医院名称", required = true)
            @PathVariable String hosname) {

        List<Hospital> list = hospitalService.findByHosname(hosname);
        return R.ok().data("list", list);
    }

    /**
     * 获取科室列表
     *
     * @param hoscode 医院code
     */
    @ApiOperation(value = "获取科室列表")
    @GetMapping("department/{hoscode}")
    public R index(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return R.ok().data("list", list);
    }

    /**
     * 医院预约挂号详情
     *
     * @param hoscode 医院code
     */
    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("{hoscode}")
    public R item(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode) {
        Map<String,Object> map = hospitalService.item(hoscode);
        return R.ok().data(map);
    }

}
