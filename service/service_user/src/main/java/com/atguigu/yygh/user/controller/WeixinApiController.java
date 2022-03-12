package com.atguigu.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utlis.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.util.ConstantPropertiesUtil;
import com.atguigu.yygh.user.util.HttpClientUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lucky845
 * @date 2022年03月12日 11:26
 */
@Api("微信登陆")
@Slf4j
@Controller
@RequestMapping("/api/userinfo/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ConstantPropertiesUtil constantPropertiesUtil;

    /**
     * 获取微信登录参数
     */
    @ApiOperation("获取微信登录参数")
    @GetMapping("/getLoginParam")
    @ResponseBody
    public R genQrConnect() throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(constantPropertiesUtil.getRedirect_url(), "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", constantPropertiesUtil.getApp_id());
        map.put("redirectUri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis() + "");
        return R.ok().data(map);
    }

    @ApiOperation("微信登陆回调接口")
    @GetMapping("/callback")
    public String callback(String code, String state) {
        // 1. 获取临时票据 code
        log.info("登录回调返回code为: {}", code);

        // 2. 拿着code和微信id及密钥,请求微信固定地址,得到两个值
        // 使用code和appid以及appscrect获取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                constantPropertiesUtil.getApp_id(),
                constantPropertiesUtil.getApp_secret(),
                code);

        // 使用HttpClient请求这个地址
        try {
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            log.info("远程调用返回的信息accesstokenInfo: {}", accessTokenInfo);
            // 从返回字符串获取两个值 openid 和 access_token
            JSONObject jsonObject = JSONObject.parseObject(accessTokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            // 判断数据库是否存在微信的扫描者的信息
            // 根据openid判断
            UserInfo userInfo = userInfoService.selectWxInfoByOpenId(openid);
            if (userInfo == null) {
                // 数据库不存在微信信息
                // 3. 拿着openid和access_token请求微信地址,得到扫描人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" + "?access_token=%s" + "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                log.info("resultInfo: {}", resultInfo);
                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
                // 解析用户信息
                // nickname 用户昵称
                String nickname = resultUserInfoJson.getString("nickname");
                // headimgurl 用户头像
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                // 获取扫描人信息添加数据库
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }
            // 返回name和token字符串
            Map<String, String> map = new HashMap<>();
            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                // 如果用户名为空就将微信名设置为用户名
                name = userInfo.getNickName();
            }
            if (StringUtils.isEmpty(name)) {
                // 如果微信名也为空就将手机号作为用户名
                name = userInfo.getPhone();
            }
            map.put("name", name);

            // 判断userInfo是否有手机号,如果有手机号为空,返回openid
            // 如果手机号不为空,返回openid值是空字符串
            // 前端判断: 如果openid不为空,绑定手机号,如果openid为空,不需要绑定手机号
            if (StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            // 使用jwt生成token字符串
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            // 跳转到前端页面
            return "redirect:http://localhost:3000/weixin/callback?token="
                    + map.get("token") + "&openid=" + map.get("openid") + "&name="
                    + URLEncoder.encode(map.get("name"), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
