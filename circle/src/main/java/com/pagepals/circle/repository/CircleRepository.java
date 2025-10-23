package com.pagepals.circle.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.Circle;

/**
 * Repository JPA pour la gestion des entités {@link Circle}.
 * 
 * Fournit les opérations de base et plusieurs requêtes personnalisées
 * pour filtrer les cercles selon leur état, leur créateur ou leur date de rencontre.
 */
public interface CircleRepository extends JpaRepository<Circle, Long> {

    /**
     * Vérifie si un créateur possède déjà un cercle prévu à la même date.
     *
     * @param createurId identifiant du créateur
     * @param dateRencontre date et heure de la rencontre
     * @return true si un cercle existe déjà à cette date pour ce créateur
     */
    boolean existsByCreateurIdAndDateRencontre(Long createurId, LocalDateTime dateRencontre);

    /**
     * Vérifie si un autre cercle du même créateur existe à la même date.
     * Utilisé lors de la mise à jour pour exclure le cercle courant.
     *
     * @param createurId identifiant du créateur
     * @param dateRencontre date et heure de la rencontre
     * @param id identifiant du cercle à exclure
     * @return true si un autre cercle existe déjà à cette date
     */
    boolean existsByCreateurIdAndDateRencontreAndIdNot(Long createurId, LocalDateTime dateRencontre, Long id);

    /**
     * Recherche tous les cercles dont la date de rencontre est passée et non encore archivés.
     *
     * @param date date actuelle
     * @return liste des cercles à archiver
     */
    List<Circle> findByDateRencontreBeforeAndIsArchivedFalse(LocalDate date);

    /**
     * Retourne tous les cercles encore actifs.
     *
     * @return liste des cercles non archivés
     */
    List<Circle> findByIsArchivedFalse();

    /**
     * Retourne tous les cercles archivés.
     *
     * @return liste des cercles archivés
     */
    List<Circle> findByIsArchivedTrue();

    /**
     * Recherche tous les cercles créés par un utilisateur donné.
     *
     * @param createurId identifiant du créateur
     * @return liste des cercles appartenant à cet utilisateur
     */
    List<Circle> findByCreateurId(Long createurId);

    /**
     * Recherche tous les cercles actifs créés par un utilisateur.
     *
     * @param userId identifiant du créateur
     * @return liste des cercles actifs créés par cet utilisateur
     */
    List<Circle> findByCreateurIdAndIsArchivedFalse(Long userId);

    /**
     * Recherche tous les cercles archivés créés par un utilisateur.
     *
     * @param userId identifiant du créateur
     * @return liste des cercles archivés créés par cet utilisateur
     */
    List<Circle> findByCreateurIdAndIsArchivedTrue(Long userId);
}
