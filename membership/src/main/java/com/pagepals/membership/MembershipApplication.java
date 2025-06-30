package com.pagepals.membership;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.pagepals.membership.client")
@SpringBootApplication
public class MembershipApplication {

	public static void main(String[] args) {
		SpringApplication.run(MembershipApplication.class, args);
	}

}
