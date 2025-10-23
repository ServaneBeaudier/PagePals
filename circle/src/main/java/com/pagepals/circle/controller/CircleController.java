package com.pagepals.circle.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pagepals.circle.dto.*;
import com.pagepals.circle.exception.CircleNotFoundException;
import com.pagepals.circle.jwt.JWTUtil;
import com.pagepals.circle.model.Circle;
import com.pagepals.circle.repository.CircleRepository;
import com.pagepals.circle.service.CircleService;
import com.pagepals.circle.service.MessageService;

import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST principal du microservice circle-service.
 * Gère les opérations liées aux cercles littéraires : création, modification,
 * recherche, suppression, gestion des messages et anonymisation des utilisateurs archivés.
 */
@RestController
@RequestMapping("api/circles")
@RequiredArgsConstructor
public class CircleController {

    private final CircleService circleService;
    private final MessageService messageService;
    private final JWTUtil jwtUtil;
    private final CircleRepository circleRepository;

    /**
     * Récupère les informations d'un cercle à partir de son identifiant.
     *
     * @param id identifiant du cercle
     * @return le cercle correspondant
     */
    @GetMapping("/{id}")
    public ResponseEntity<CircleDTO> getCircleById(@PathVariable Long id) {
        CircleDTO dto = circleService.getCircleById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Retourne la liste des cercles actuellement actifs (non archivés).
     *
     * @return liste des cercles actifs
     */
    @GetMapping("active")
    public ResponseEntity<List<CircleDTO>> getCirclesActive() {
        return ResponseEntity.ok(circleService.getCirclesActive());
    }

    /**
     * Retourne la liste des cercles archivés.
     *
     * @return liste des cercles archivés
     */
    @GetMapping("archived")
    public ResponseEntity<List<CircleDTO>> getCirclesArchived() {
        return ResponseEntity.ok(circleService.getCirclesArchived());
    }

    /**
     * Crée un nouveau cercle littéraire et inscrit automatiquement son créateur.
     *
     * @param dto données du cercle à créer
     * @param authHeader en-tête d'autorisation contenant le jeton JWT
     * @return 201 CREATED si la création est réussie
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createCircle(@RequestBody CreateCircleDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);

        circleService.createCircle(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Met à jour les informations d'un cercle existant.
     *
     * @param dto données mises à jour du cercle
     * @param id identifiant du cercle à modifier
     * @param userId identifiant de l'utilisateur effectuant la mise à jour
     * @return 204 NO CONTENT si la mise à jour est réussie
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCircle(@RequestBody UpdateCircleDTO dto, @PathVariable long id,
            @RequestHeader("X-User-Id") Long userId) {
        dto.setId(id);
        circleService.updateCircle(dto, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère la liste des messages associés à un cercle donné.
     *
     * @param circleId identifiant du cercle
     * @return liste des messages du cercle
     */
    @GetMapping("/{circleId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long circleId) {
        return ResponseEntity.ok(messageService.getMessagesByCircleId(circleId));
    }

    /**
     * Envoie un message dans un cercle donné.
     *
     * @param circleId identifiant du cercle
     * @param dto contenu du message à envoyer
     * @param authHeader en-tête d'autorisation contenant le jeton JWT de l'auteur
     * @return 200 OK si le message a été envoyé
     */
    @PostMapping("/{circleId}/messages")
    public ResponseEntity<Void> envoyerMessage(@PathVariable Long circleId,
            @RequestBody MessageDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Long auteurId = jwtUtil.extractUserId(token);

        dto.setCircleId(circleId);
        dto.setAuteurId(auteurId);

        messageService.sendMessage(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Recherche des cercles selon différents critères.
     *
     * @param criteria critères de recherche (titre, genre, date, etc.)
     * @return liste des cercles correspondant à la recherche
     */
    @PostMapping("/search")
    public ResponseEntity<List<CircleDTO>> searchCircles(@RequestBody SearchCriteriaDTO criteria) {
        List<CircleDTO> resultats = circleService.searchCircles(criteria);
        return ResponseEntity.ok(resultats);
    }

    /**
     * Récupère le nombre maximum de membres autorisés pour un cercle donné.
     *
     * @param id identifiant du cercle
     * @return le nombre maximum de membres
     * @throws CircleNotFoundException si le cercle n'existe pas
     */
    @GetMapping("/{id}/max-membres")
    public int getMaxMembres(@PathVariable Long id) {
        Circle circle = circleRepository.findById(id)
                .orElseThrow(() -> new CircleNotFoundException("Cercle introuvable"));
        return circle.getNbMaxMembres();
    }

    /**
     * Récupère les cercles créés par un utilisateur spécifique.
     *
     * @param userId identifiant du créateur
     * @return liste des cercles créés par cet utilisateur
     */
    @GetMapping("/created-by/{userId}")
    public List<CircleDTO> getCirclesByCreateur(@PathVariable Long userId) {
        return circleService.findCirclesByCreateur(userId);
    }

    /**
     * Supprime tous les cercles actifs appartenant à un créateur donné.
     *
     * @param userId identifiant du créateur
     * @return 204 NO CONTENT si la suppression est réussie
     */
    @DeleteMapping("/active-by-createur/{userId}")
    public ResponseEntity<Void> deleteActiveCirclesByCreateur(@PathVariable Long userId) {
        circleService.deleteActiveCirclesByCreateur(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Anonymise les données d'un utilisateur dans les cercles archivés.
     *
     * @param userId identifiant de l'utilisateur à anonymiser
     * @return 204 NO CONTENT si l'opération est réussie
     */
    @PutMapping("/anonymize-user/{userId}")
    public ResponseEntity<Void> anonymizeUserInArchivedCircles(@PathVariable Long userId) {
        circleService.anonymizeUserInArchivedCircles(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime définitivement un cercle à partir de son identifiant.
     *
     * @param id identifiant du cercle à supprimer
     * @return 204 NO CONTENT si la suppression est réussie
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCircle(@PathVariable Long id) {
        circleService.deleteCircleById(id);
        return ResponseEntity.noContent().build();
    }
}
