package com.pagepals.circle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.pagepals.circle.client")
public class CircleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircleApplication.class, args);
	}

}
