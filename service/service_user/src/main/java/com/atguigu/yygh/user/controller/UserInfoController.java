package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lucky845
 * @since 2022-03-11
 */
@Api(tags = "用户信息")
@RestController
@RequestMapping("/api/userinfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 会员登录
     *
     * @param loginVo 登录信息
     */
    @ApiOperation("会员登录")
    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo) {
        Map<String, Object> info = userInfoService.login(loginVo);
        return R.ok().data(info);
    }
}

