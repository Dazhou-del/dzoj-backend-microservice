package com.dazhou.dzojbackendjudgeservice;

import com.dazhou.dzojbackendjudgeservice.rabbitmq.InitRabbitMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author da zhou
 */
@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.dazhou")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.dazhou.dzojbackendserviceclient.service"})
public class DzojBackendJudgeServiceApplication {

	public static void main(String[] args) {
		// 初始化消息队列
		InitRabbitMq.doInit();
		SpringApplication.run(DzojBackendJudgeServiceApplication.class, args);
	}

}
