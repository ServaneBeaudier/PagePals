package com.pagepals.circle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Point d’entrée du microservice Circle.
 * 
 * Ce service gère la création, la recherche, la mise à jour et la
 * communication (messages) des cercles littéraires.
 * 
 * - @EnableFeignClients : permet la communication avec d'autres microservices (user, membership)
 * - @EnableScheduling : active les tâches planifiées, notamment l’archivage automatique des cercles passés
 */
@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.pagepals.circle.client")
public class CircleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircleApplication.class, args);
	}

}
