package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员模块")
//@CrossOrigin // 跨域: 域名、端口、协议任一不同，都是跨域 (前后端分离开发中，需要考虑ajax跨域的问题)
@RequestMapping("/admin/user")
@RestController
public class UserController {

    @ApiOperation(value = "管理员登陆")
    @PostMapping("/login")
    public R login() {
        return R.ok().data("token", "admin-token");
    }

    @ApiOperation(value = "管理员登陆信息展示")
    @GetMapping("/info")
    public R info(
            @ApiParam(name = "token",value = "登陆信息",required = true)
            String token) {
        return R.ok().data("roles", "[admin]")
                .data("introduction", "I am a super administrator")
                .data("avatar", "https://img.114yygh.com/staticres/web/favicon.ico");
    }

}
