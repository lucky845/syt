package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lucky845
 * @since 2022-03-11
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {


    /**
     * 会员登录
     *
     * @param loginVo 登录信息
     */
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        // 校验参数
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YYGHException(200001, "手机号或验证码为空");
        }

        // todo 校验验证码

        // 判断手机号是否已经被使用
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("phone", phone);
        // 获取会员信息
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        if (null == userInfo) {
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setName("");
            userInfo.setStatus(1);
            this.save(userInfo);
        }
        // 检验用户是否被禁用
        if (userInfo.getStatus() == 0) {
            throw new YYGHException(20001, "用户已被禁用");
        }

        // 返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        // 如果没有设置过用户名,就使用手机号作为用户名
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        // JWT生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);

        return map;
    }
}
