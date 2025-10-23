package com.pagepals.auth.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pagepals.auth.client.UserProfileClient;
import com.pagepals.auth.dto.AuthResponseDTO;
import com.pagepals.auth.dto.LoginDTO;
import com.pagepals.auth.dto.RegisterDTO;
import com.pagepals.auth.dto.UpdateMailDTO;
import com.pagepals.auth.dto.UpdatePasswordDTO;
import com.pagepals.auth.dto.UserProfileCreateRequest;
import com.pagepals.auth.exception.EmailAlreadyUsedException;
import com.pagepals.auth.exception.InvalidCredentialException;
import com.pagepals.auth.exception.UserNotFoundException;
import com.pagepals.auth.jwt.JWTGenerator;
import com.pagepals.auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.pagepals.auth.model.Role;
import com.pagepals.auth.model.UserEntity;

/**
 * Implémentation de la logique métier du service d'authentification.
 * Gère l'inscription, la connexion, la mise à jour des informations utilisateur,
 * la suppression de comptes et la génération de jetons JWT.
 *
 * Interagit avec le microservice user via le client Feign pour la création
 * automatique du profil utilisateur après inscription.
 */
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;
    private final UserProfileClient userProfileClient;

    /**
     * Inscrit un nouvel utilisateur après validation des données.
     * Crée également le profil correspondant via le microservice user.
     *
     * @param dto données d'inscription (email, mot de passe, pseudo)
     * @return la réponse d'authentification avec jetons et informations utilisateur
     * @throws EmailAlreadyUsedException si l'adresse e-mail est déjà enregistrée
     * @throws IllegalArgumentException si le pseudo est manquant ou vide
     */
    @Override
    public AuthResponseDTO register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException("Cet email est déjà utilisé !");
        }

        if (dto.getPseudo() == null || dto.getPseudo().isBlank()) {
            throw new IllegalArgumentException("Le pseudo est obligatoire !");
        }

        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        user.setRole(Role.MEMBRE);
        userRepository.save(user);

        UserProfileCreateRequest profileRequest = new UserProfileCreateRequest();
        profileRequest.setId(user.getId());
        profileRequest.setPseudo(dto.getPseudo());
        profileRequest.setDateInscription(LocalDate.now());
        userProfileClient.createUserProfile(profileRequest);

        String accessToken = jwtGenerator.generateToken(
                user.getId(),
                user.getRole().name(),
                user.getEmail(),
                15 * 60 * 1000 // 15 minutes
        );

        String refreshToken = jwtGenerator.generateToken(
                user.getId(),
                user.getRole().name(),
                user.getEmail(),
                7 * 24 * 60 * 60 * 1000 // 7 jours
        );

        return new AuthResponseDTO(accessToken, refreshToken, user.getEmail(), user.getRole().name(), user.getId());
    }

    /**
     * Authentifie un utilisateur à partir de ses identifiants.
     *
     * @param dto données de connexion (email et mot de passe)
     * @return les jetons JWT et informations utilisateur
     * @throws InvalidCredentialException si les identifiants sont incorrects
     */
    @Override
    public AuthResponseDTO login(LoginDTO dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialException("Identifiants incorrects"));

        if (!passwordEncoder.matches(dto.getMotDePasse(), user.getMotDePasse())) {
            throw new InvalidCredentialException("Identifiants incorrects");
        }

        String accessToken = jwtGenerator.generateToken(user.getId(), user.getRole().name(), user.getEmail(),
                15 * 60 * 1000);
        String refreshToken = jwtGenerator.generateToken(user.getId(), user.getRole().name(), user.getEmail(),
                7 * 24 * 60 * 60 * 1000);

        return new AuthResponseDTO(accessToken, refreshToken, user.getEmail(), user.getRole().name(), user.getId());
    }

    /**
     * Récupère l'adresse e-mail associée à un identifiant utilisateur.
     *
     * @param userId identifiant unique de l'utilisateur
     * @return l'adresse e-mail si trouvée, sinon null
     */
    @Override
    public String getEmailByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(UserEntity::getEmail)
                .orElse(null);
    }

    /**
     * Met à jour l'adresse e-mail d'un utilisateur existant.
     *
     * @param dto données nécessaires à la mise à jour
     * @throws UserNotFoundException si l'utilisateur n'existe pas
     */
    @Override
    public void updateMail(UpdateMailDTO dto) {
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

        user.setEmail(dto.getNewEmail());
        userRepository.save(user);
    }

    /**
     * Met à jour le mot de passe d'un utilisateur après chiffrement.
     *
     * @param dto données nécessaires à la modification du mot de passe
     * @throws UserNotFoundException si l'utilisateur n'existe pas
     */
    @Override
    public void updatePassword(UpdatePasswordDTO dto) {
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

        user.setMotDePasse(passwordEncoder.encode(dto.getNewMotDePasse()));
        userRepository.save(user);
    }

    /**
     * Supprime un utilisateur à partir de son identifiant.
     *
     * @param userId identifiant de l'utilisateur à supprimer
     * @throws EntityNotFoundException si l'utilisateur n'existe pas
     */
    @Override
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Utilisateur introuvable avec l'id : " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Recherche un utilisateur par identifiant.
     *
     * @param id identifiant de l'utilisateur
     * @return l'entité utilisateur si trouvée, sinon null
     */
    @Override
    public UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Analyse et valide un jeton JWT, puis renvoie les claims extraits.
     *
     * @param token jeton JWT à analyser
     * @return les claims contenus dans le jeton
     */
    @Override
    public io.jsonwebtoken.Claims parseToken(String token) {
        return jwtGenerator.parseToken(token);
    }

    /**
     * Génère un nouveau jeton d'accès (durée courte).
     *
     * @param user utilisateur concerné
     * @return jeton JWT d'accès
     */
    @Override
    public String generateAccessToken(UserEntity user) {
        return jwtGenerator.generateToken(user.getId(), user.getRole().name(), user.getEmail(), 15 * 60 * 1000);
    }

    /**
     * Génère un nouveau jeton de rafraîchissement (durée longue).
     *
     * @param user utilisateur concerné
     * @return jeton JWT de rafraîchissement
     */
    @Override
    public String generateRefreshToken(UserEntity user) {
        return jwtGenerator.generateToken(user.getId(), user.getRole().name(), user.getEmail(),
                7 * 24 * 60 * 60 * 1000);
    }
}
