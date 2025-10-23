package com.pagepals.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.auth.model.UserEntity;

/**
 * Interface d'accès aux données pour l'entité utilisateur.
 * Fournit les opérations CRUD de base et des méthodes de recherche spécifiques
 * via Spring Data JPA.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Recherche un utilisateur à partir de son adresse e-mail.
     *
     * @param email adresse e-mail de l'utilisateur recherché
     * @return un Optional contenant l'utilisateur s'il existe, sinon vide
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Vérifie si une adresse e-mail est déjà utilisée par un utilisateur existant.
     *
     * @param email adresse e-mail à vérifier
     * @return true si l'adresse est déjà utilisée, false sinon
     */
    boolean existsByEmail(String email);
}
