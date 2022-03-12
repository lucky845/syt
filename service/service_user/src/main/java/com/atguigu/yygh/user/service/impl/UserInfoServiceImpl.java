package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utlis.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private RedisTemplate redisTemplate;

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

        // 校验验证码
        String mobleCode = (String) redisTemplate.opsForValue().get(phone);
        if (!code.equals(mobleCode)) {
            throw new YYGHException(20001, "验证码错误");
        }

        String openid = loginVo.getOpenid();
        Map<String, Object> map = new HashMap<>();
        // openid为空,用户未使用微信登陆,而是使用手机号登陆
        if (StringUtils.isEmpty(openid)) {
            // 根据手机查询数据库
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            UserInfo userInfo = baseMapper.selectOne(wrapper);
            // 如果返回对象为空,就是第一次登陆, 存储到数据库
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
            // 判断用户是否可用
            if (userInfo.getStatus() == 0) {
                throw new YYGHException(20001, "用户已经被禁用");
            }
            map = get(userInfo);
        } else {
            // 用户使用微信登陆
            // 1. 创建UserInfo对象,用于存储最终所有数据
            UserInfo userInfoFinal = new UserInfo();

            // 2. 根据手机号查询数据
            // 如果查询到数据,封装到userInfoFinal
            UserInfo userInfoPhone = baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("phone", phone));
            if (userInfoPhone != null) {
                // 如果查询手机号数据不为空,封装到userInfoFinal
                BeanUtils.copyProperties(userInfoPhone, userInfoFinal);
                // 把手机号数据删除
                baseMapper.delete(new QueryWrapper<UserInfo>().eq("phone", phone));
            }

            // 3. 根据openid查询微信信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid", openid);
            UserInfo userInfoWX = baseMapper.selectOne(wrapper);

            // 4. 把微信数据封装到userInfoFinal
            userInfoFinal.setOpenid(userInfoWX.getOpenid());
            userInfoFinal.setNickName(userInfoWX.getNickName());
            userInfoFinal.setId(userInfoWX.getId());
            // 数据库表没有相同绑定手机号,设置值
            if (userInfoPhone == null) {
                userInfoFinal.setPhone(phone);
                userInfoFinal.setStatus(userInfoWX.getStatus());
            }
            // 修改手机号
            baseMapper.updateById(userInfoFinal);

            // 5. 判断用户是否锁定
            if (userInfoFinal.getStatus() == 0) {
                throw new YYGHException(20001, "用户被锁定");
            }
            // 6. 登录后,返回登陆数据
            map = get(userInfoFinal);
        }
        return map;
    }

    /**
     * 封装登陆数据
     *
     * @param userInfo 用户信息
     */
    private Map<String, Object> get(UserInfo userInfo) {
        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //根据userid和name生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    /**
     * 根据openid查询微信信息
     */
    @Override
    public UserInfo selectWxInfoByOpenId(String openid) {
        return baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("openid", openid));
    }
}
