package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author lucky845
 * @date 2022年03月15日 9:27
 */
@Api(tags = "后台会员管理")
@RestController
@RequestMapping("/admin/userinfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 用户列表（条件查询带分页）
     *
     * @param page            当前页码
     * @param limit           每页记录数
     * @param userInfoQueryVo 查询条件对象
     */
    @GetMapping("{page}/{limit}")
    public R list(@PathVariable Long page,
                  @PathVariable Long limit,
                  UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageModel =
                userInfoService.selectPage(page, limit, userInfoQueryVo);
        return R.ok().data("pageModel", pageModel);
    }

    /**
     * 修改就诊人状态锁定
     *
     * @param userId 用户id
     * @param status 状态
     */
    @ApiOperation("锁定")
    @GetMapping("/lock/{userId}/{status}")
    public R lock(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable("userId") Long userId,

            @ApiParam(value = "状态", required = true)
            @PathVariable("status") Integer status) {
        userInfoService.lock(userId, status);
        return R.ok();
    }

    /**
     * 用户详情
     *
     * @param userId 用户id
     */
    @GetMapping("show/{userId}")
    public R show(@PathVariable Long userId) {
        Map<String, Object> map = userInfoService.show(userId);
        return R.ok().data(map);
    }

    /**
     * 用户认证状态审批
     *
     * @param userId     用户id
     * @param authStatus 认证状态
     */
    @ApiOperation("用户信息认证审批")
    @GetMapping("/approval/{userId}/{authStatus}")
    public R approval(
            @ApiParam(value = "用户id", required = true)
            @PathVariable Long userId,

            @ApiParam(value = "认证状态", required = true)
            @PathVariable Integer authStatus) {
        userInfoService.approval(userId, authStatus);
        return R.ok();
    }


}
