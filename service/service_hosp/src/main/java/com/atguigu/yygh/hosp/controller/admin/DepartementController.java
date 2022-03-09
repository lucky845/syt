package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lucky845
 * @date 2022年03月09日 9:27
 */
@Api(tags = "医院科室信息")
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartementController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 根据医院编号，查询医院所有科室列表
     * @param hoscode 医院编号
     */
    @ApiOperation(value = "查询医院所有科室列表")
    @GetMapping("getDeptList/{hoscode}")
    public R getDeptList(
            @ApiParam(value = "医院编号", required = true)
            @PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return R.ok().data("list",list);
    }

}
