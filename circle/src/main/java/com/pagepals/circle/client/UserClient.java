package com.pagepals.circle.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pagepals.circle.dto.MessageDTO;

/**
 * Client Feign utilisé pour communiquer avec le microservice utilisateur (user-service).
 * 
 * Permet de récupérer les informations de plusieurs utilisateurs nécessaires
 * à l'affichage ou à la composition des messages dans le circle-service.
 */
@FeignClient(name = "user", path = "/api/user")
public interface UserClient {

    /**
     * Récupère les informations de plusieurs utilisateurs à partir de leurs identifiants.
     * 
     * @param userIds liste des identifiants des utilisateurs concernés
     * @return liste des objets MessageDTO contenant les informations à afficher
     */
    @GetMapping("/message")
    List<MessageDTO> getInfosPourMessage(@RequestParam("ids") List<Long> userIds);
}
