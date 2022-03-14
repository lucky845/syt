package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
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

    /**
     * 用户认证
     *
     * @param userId     用户id
     * @param userAuthVo 用户认证信息
     */
    void userAuth(Long userId, UserAuthVo userAuthVo);

    /**
     * 根据用户id获取用户状态
     *
     * @param userId 用户id
     */
    UserInfo getById(Long userId);

}
