package com.atguigu.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients(basePackages = "com.atguigu") // 可以保证当前模块能够扫描当前模块依赖的jar包中的FeignClient接口了
@EnableDiscoveryClient // nacos
@EnableTransactionManagement // 事务
@MapperScan(basePackages = "com.atguigu.yygh.hosp.mapper")
@ComponentScan(basePackages = {"com.atguigu"})
@SpringBootApplication // 组合注解: 相当于 @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}