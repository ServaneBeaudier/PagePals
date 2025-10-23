package com.pagepals.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Point d'entrée du microservice d'authentification de l'application PagePals.
 * 
 * Ce service gère l'inscription, la connexion et la gestion des comptes utilisateurs.
 * Il communique avec d'autres microservices via Feign et est enregistré auprès d'Eureka
 * pour la découverte de services.
 */
@EnableFeignClients(basePackages = "com.pagepals.auth.client")
@EnableDiscoveryClient
@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}
