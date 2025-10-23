package com.pagepals.auth.client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;

import com.pagepals.auth.dto.UserProfileCreateRequest;


/**
 * Client Feign pour communiquer avec le microservice user-service.
 * 
 * Cette interface définit les appels HTTP distants nécessaires à la création
 * du profil utilisateur lors de l'inscription. Elle permet à {@code auth-service}
 * de déléguer la persistance des données de profil au service utilisateur.
 * 
 * Le nom du client ({@code user}) doit correspondre au nom enregistré du
 * microservice dans Eureka, afin que la découverte de service fonctionne correctement.
 *
 * @see com.pagepals.auth.dto.UserProfileCreateRequest
 */
@FeignClient(name = "user")
public interface UserProfileClient {

    /**
     * Envoie une requête HTTP POST au microservice utilisateur afin de créer un
     * nouveau profil utilisateur correspondant à l'utilisateur fraîchement inscrit.
     *
     * @param request l'objet contenant les informations de profil à créer (identifiant utilisateur, nom, etc.)
     */
    @PostMapping("/api/user/create")
    void createUserProfile(@RequestBody UserProfileCreateRequest request);
}
