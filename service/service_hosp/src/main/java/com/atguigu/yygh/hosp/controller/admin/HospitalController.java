package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lucky845
 * @date 2022年03月08日 9:57
 */
@Api(tags = "医院接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * 获取分页列表
     *
     * @param page            当前页码
     * @param limit           每页记录数
     * @param hospitalQueryVo 查询条件
     */
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public R index(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Integer page,

            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Integer limit,

            @ApiParam(value = "查询条件", required = true)
                    HospitalQueryVo hospitalQueryVo) {
        // 调用方法
        return R.ok().data("pages", hospitalService.selectPage(page, limit, hospitalQueryVo));
    }

    /**
     * 更新医院状态
     *
     * @param id     医院id
     * @param status 医院状态
     */
    @ApiOperation(value = "更新医院状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public R updateStatus(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable("id") String id,

            @ApiParam(name = "status", value = "状态（0：未上线 1：已上线）", required = true)
            @PathVariable("status") Integer status) {
        hospitalService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 获取医院详情
     *
     * @param id 医院id
     */
    @ApiOperation(value = "获取医院详情")
    @GetMapping("show/{id}")
    public R show(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable String id) {
        return R.ok().data("hospital", hospitalService.show(id));
    }

}
