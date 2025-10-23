package com.pagepals.auth.service;

import com.pagepals.auth.dto.AuthResponseDTO;
import com.pagepals.auth.dto.LoginDTO;
import com.pagepals.auth.dto.RegisterDTO;
import com.pagepals.auth.dto.UpdateMailDTO;
import com.pagepals.auth.dto.UpdatePasswordDTO;
import com.pagepals.auth.model.UserEntity;

import io.jsonwebtoken.Claims;

/**
 * Interface définissant la logique métier du service d'authentification.
 * 
 * Regroupe les opérations de gestion des utilisateurs, de génération et de
 * validation des jetons JWT, ainsi que la mise à jour des informations sensibles.
 */
public interface AuthService {

    /**
     * Inscrit un nouvel utilisateur et génère les jetons d'authentification associés.
     *
     * @param dto données d'inscription de l'utilisateur
     * @return la réponse d'authentification contenant les jetons et les informations utilisateur
     */
    AuthResponseDTO register(RegisterDTO dto);

    /**
     * Authentifie un utilisateur à partir de ses identifiants.
     *
     * @param dto informations de connexion (email et mot de passe)
     * @return la réponse d'authentification contenant les jetons et les informations utilisateur
     */
    AuthResponseDTO login(LoginDTO dto);

    /**
     * Met à jour l'adresse e-mail d'un utilisateur.
     *
     * @param dto données nécessaires à la modification de l'e-mail
     */
    void updateMail(UpdateMailDTO dto);

    /**
     * Met à jour le mot de passe d'un utilisateur.
     *
     * @param dto données nécessaires à la modification du mot de passe
     */
    void updatePassword(UpdatePasswordDTO dto);

    /**
     * Récupère l'adresse e-mail d'un utilisateur à partir de son identifiant.
     *
     * @param userId identifiant unique de l'utilisateur
     * @return adresse e-mail correspondante, ou null si l'utilisateur n'existe pas
     */
    String getEmailByUserId(Long userId);

    /**
     * Supprime définitivement les données d'un utilisateur dans la base d'authentification.
     *
     * @param userId identifiant unique de l'utilisateur à supprimer
     */
    void deleteUserById(Long userId);

    /**
     * Analyse et valide un jeton JWT afin d'en extraire les informations qu'il contient.
     *
     * @param token jeton JWT à analyser
     * @return les claims extraits du token
     */
    Claims parseToken(String token);

    /**
     * Recherche un utilisateur à partir de son identifiant.
     *
     * @param id identifiant de l'utilisateur
     * @return l'entité utilisateur correspondante, ou null si elle n'existe pas
     */
    UserEntity findUserById(Long id);

    /**
     * Génère un jeton d'accès (access token) pour l'utilisateur spécifié.
     *
     * @param user entité utilisateur pour laquelle le jeton est généré
     * @return jeton JWT signé
     */
    String generateAccessToken(UserEntity user);

    /**
     * Génère un jeton de rafraîchissement (refresh token) pour l'utilisateur spécifié.
     *
     * @param user entité utilisateur pour laquelle le jeton est généré
     * @return jeton JWT signé avec une durée plus longue
     */
    String generateRefreshToken(UserEntity user);
}
