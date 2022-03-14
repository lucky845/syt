package com.atguigu.yygh.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lucky845
 * @date 2022年03月14日 10:20
 */
@Component
@Data
@ConfigurationProperties(prefix = "aliyun.oss.file")
public class OSSProperties {

    private String endpoint;
    private String keyid;
    private String keysecret;
    private String bucketname;

}
