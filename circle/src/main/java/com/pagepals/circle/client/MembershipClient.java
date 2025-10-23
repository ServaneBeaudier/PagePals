package com.pagepals.circle.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Client Feign permettant la communication avec le microservice de gestion des adhésions (membership-service).
 * 
 * Utilisé par le circle-service pour inscrire automatiquement un créateur à son propre cercle
 * et pour obtenir le nombre de membres d'un cercle donné.
 */
@FeignClient(name = "membership", path = "/api/memberships")
public interface MembershipClient {

    /**
     * Ajoute automatiquement le créateur d'un cercle comme membre de celui-ci.
     *
     * @param circleId identifiant du cercle nouvellement créé
     * @param userId identifiant du créateur du cercle
     */
    @PostMapping("/auto-inscription")
    void ajouterCreateurCommeMembre(@RequestParam("circleId") long circleId, @RequestParam("userId") long userId);

    /**
     * Retourne le nombre de membres inscrits à un cercle.
     *
     * @param circleId identifiant du cercle
     * @return le nombre total de membres associés à ce cercle
     */
    @GetMapping("/count/{circleId}")
    int countMembersForCircle(@PathVariable("circleId") Long circleId);
}
