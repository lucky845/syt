package com.atguigu.yygh.user.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lucky845
 * @date 2022年03月12日 11:24
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.open")
public class ConstantPropertiesUtil {

    private String app_id;

    private String app_secret;

    private String redirect_url;

}
