package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lucky845
 * @since 2022-03-11
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 会员登录
     *
     * @param loginVo 登录信息
     */
    Map<String, Object> login(LoginVo loginVo);

    /**
     * 根据openid查询微信信息
     */
    UserInfo selectWxInfoByOpenId(String openid);
}
