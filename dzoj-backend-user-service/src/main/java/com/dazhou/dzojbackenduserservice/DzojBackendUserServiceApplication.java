package com.dazhou.dzojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.dazhou.dzojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.dazhou")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.dazhou.dzojbackendserviceclient.service"})
public class DzojBackendUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DzojBackendUserServiceApplication.class, args);
	}

}
