package com.dazhou.dzojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.dazhou.dzojbackendquestionservice.mapper")
//定时任务
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.dazhou")
//将服务注册到注册中心去
@EnableDiscoveryClient
//使用注解@EnableFeignClients启用feign客户端
@EnableFeignClients(basePackages = {"com.dazhou.dzojbackendserviceclient.service"})
public class DzojBackendQuestionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DzojBackendQuestionServiceApplication.class, args);
	}

}
