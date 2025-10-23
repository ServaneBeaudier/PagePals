package com.pagepals.circle.service;

import java.util.List;

import com.pagepals.circle.dto.CircleDTO;
import com.pagepals.circle.dto.CreateCircleDTO;
import com.pagepals.circle.dto.SearchCriteriaDTO;
import com.pagepals.circle.dto.UpdateCircleDTO;

/**
 * Interface définissant la logique métier liée à la gestion des cercles littéraires.
 * 
 * Fournit les opérations principales : création, modification, suppression,
 * anonymisation, recherche et archivage de cercles.
 */
public interface CircleService {

    /**
     * Crée un nouveau cercle littéraire et inscrit automatiquement le créateur comme membre.
     *
     * @param dto données de création du cercle
     * @param createurId identifiant du créateur du cercle
     */
    void createCircle(CreateCircleDTO dto, long createurId);

    /**
     * Met à jour les informations d’un cercle existant.
     *
     * @param dto données mises à jour du cercle
     * @param createurId identifiant du créateur effectuant la modification
     */
    void updateCircle(UpdateCircleDTO dto, long createurId);

    /**
     * Supprime un cercle à partir de son identifiant.
     *
     * @param id identifiant du cercle à supprimer
     */
    void deleteCircleById(Long id);

    /**
     * Supprime tous les cercles actifs créés par un utilisateur donné.
     *
     * @param userId identifiant du créateur
     */
    void deleteActiveCirclesByCreateur(Long userId);

    /**
     * Anonymise les données d’un utilisateur dans les cercles archivés.
     * Utilisé notamment lorsqu’un utilisateur supprime son compte.
     *
     * @param userId identifiant de l’utilisateur à anonymiser
     */
    void anonymizeUserInArchivedCircles(Long userId);

    /**
     * Récupère les informations d’un cercle par son identifiant.
     *
     * @param id identifiant du cercle
     * @return un objet CircleDTO représentant le cercle
     */
    CircleDTO getCircleById(long id);

    /**
     * Retourne la liste de tous les cercles actifs.
     *
     * @return liste des cercles non archivés
     */
    List<CircleDTO> getCirclesActive();

    /**
     * Retourne la liste de tous les cercles archivés.
     *
     * @return liste des cercles archivés
     */
    List<CircleDTO> getCirclesArchived();

    /**
     * Archive automatiquement les cercles dont la date de rencontre est passée.
     */
    void archiverCerclesPasses();

    /**
     * Recherche des cercles en fonction de critères multiples (genre, format, date, etc.).
     *
     * @param criteria critères de recherche
     * @return liste des cercles correspondant à la recherche
     */
    List<CircleDTO> searchCircles(SearchCriteriaDTO criteria);

    /**
     * Recherche tous les cercles créés par un utilisateur donné.
     *
     * @param createurId identifiant du créateur
     * @return liste des cercles créés par cet utilisateur
     */
    List<CircleDTO> findCirclesByCreateur(Long createurId);
}
