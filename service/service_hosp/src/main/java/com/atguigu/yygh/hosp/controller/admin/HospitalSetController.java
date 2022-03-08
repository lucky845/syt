package com.atguigu.yygh.hosp.controller.admin;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.utils.MD5;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author lucky845
 * @since 2022-02-28
 */
@Api(tags = "医院设置接口")
//@CrossOrigin // 跨域: 域名、端口、协议任一不同，都是跨域 (前后端分离开发中，需要考虑ajax跨域的问题)
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 查询所有医院设置
     */
    @ApiOperation(value = "查询所有的医院设置列表")
    @GetMapping("/list")
    public R getHospitalSetList() {

//        try {
//            int i = 1 / 0;
//        } catch (Exception ex) {
//            throw new YYGHException(20001, "预约失败");
//        }

        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list", list);
    }

    /**
     * 医院设置逻辑删除功能
     *
     * @param id 医院设置id
     */
    @ApiOperation(value = "根据医院设置id删除指定的医院设置信息")
    @DeleteMapping("/delete/{id}")
    public R deleteById(
            @ApiParam(name = "id", value = "医院设置id", required = true)
            @PathVariable Long id) {
        hospitalSetService.removeById(id);
        return R.ok();
    }

    /**
     * 带查询条件的医院设置分页信息
     *
     * @param page               当前页码
     * @param limit              每页记录数
     * @param hospitalSetQueryVo 查询条件对象
     */
    @ApiOperation(value = "带查询条件的医院设置分页信息")
    @PostMapping("/{page}/{limit}")
    public R getHospitalSetPage(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "hospitalSetQueryVo", value = "查询条件对象", required = false)
            @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {

        Page<HospitalSet> pageParam = new Page<>(page, limit);

        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        // 判断医院名称是否为空
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())) {
            queryWrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        // 判断医院编号是否为空
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())) {
            queryWrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }
        hospitalSetService.page(pageParam, queryWrapper);
        return R.ok().data("total", pageParam.getTotal()).data("rows", pageParam.getRecords());
    }

    /**
     * 新增医院设置信息
     *
     * @param hospitalSet 医院设置对象
     */
    @ApiOperation(value = "新增医院设置信息")
    @PostMapping("/save")
    public R saveHospitalSet(
            @ApiParam(name = "hospitaSet", value = "医院设置对象", required = true)
            @RequestBody HospitalSet hospitalSet) {

        // 设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);
        // 签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        // 保存医院设置信息
        hospitalSetService.save(hospitalSet);
        return R.ok();
    }

    /**
     * 根据id查询医院设置
     *
     * @param id 医院设置id
     */
    @ApiOperation(value = "根据id获取医院设置信息")
    @GetMapping("/info/{id}")
    public R getHospitalSetById(
            @ApiParam(name = "id", value = "医院设置id", required = true)
            @PathVariable Long id) {

        HospitalSet item = hospitalSetService.getById(id);
        return R.ok().data("item", item);
    }

    /**
     * 根据id修改医院设置
     *
     * @param hospitalSet 医院设置对象
     */
    @ApiOperation(value = "根据id修改医院设置")
    @PostMapping("/update")
    public R updateById(
            @ApiParam(name = "hospitalSet", value = "医院设置对象", required = true)
            @RequestBody HospitalSet hospitalSet) {

        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    /**
     * 批量删除设置
     *
     * @param ids 医院设置集合
     */
    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchDelete")
    public R batchDelete(
            @ApiParam(name = "ids", value = "医院设置id集合", required = true)
            @RequestBody List<Long> ids) {
        hospitalSetService.removeByIds(ids);
        return R.ok();
    }

    /**
     * 医院设置锁定和解锁
     *
     * @param id     医院设置id
     * @param status 医院设置状态
     */
    @ApiOperation(value = "医院设置锁定和解锁")
    @PutMapping("/modify/{id}/{status}")
    public R modify(
            @ApiParam(name = "id", value = "医院设置id", required = true)
            @PathVariable Long id,

            @ApiParam(name = "status", value = "医院设置状态", required = true)
            @PathVariable Integer status) {

        // 根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        // 设置状态
        hospitalSet.setStatus(status);
        // 调用方法
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

}

