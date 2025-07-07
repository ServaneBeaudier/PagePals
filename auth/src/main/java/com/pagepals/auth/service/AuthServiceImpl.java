package com.pagepals.auth.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

import com.pagepals.auth.model.Role;
import com.pagepals.auth.model.UserEntity;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTGenerator jwtGenerator;

    private final UserProfileClient userProfileClient;

    @Override
    public AuthResponseDTO register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException("Cet email est déjà utilisé !");
        }

        if (dto.getPseudo() == null || dto.getPseudo().isBlank()) {
            throw new IllegalArgumentException("Le pseudo est obligatoire !");
        }

        // Création et sauvegarde de l'utilisateur
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        user.setRole(Role.MEMBRE);
        userRepository.save(user);

        // Création du profil utilisateur côté user-service
        UserProfileCreateRequest profileRequest = new UserProfileCreateRequest();
        profileRequest.setId(user.getId());
        profileRequest.setPseudo(dto.getPseudo());
        profileRequest.setDateInscription(LocalDate.now());

        userProfileClient.createUserProfile(profileRequest);

        // Génération du token JWT
        String token = jwtGenerator.generateToken(user.getId(), user.getRole().name(), user.getEmail());

        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name(), user.getId());
    }

    @Override
    public AuthResponseDTO login(LoginDTO dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialException("Identifiants incorrects"));

        if (!passwordEncoder.matches(dto.getMotDePasse(), user.getMotDePasse())) {
            throw new InvalidCredentialException("Identifiants incorrects");
        }

        String token = jwtGenerator.generateToken(user.getId(), user.getRole().name(), user.getEmail());

        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name(), user.getId());
    }

    @Override
    public String getEmailByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getEmail())
                .orElse(null);
    }

    @Override
    public void updateMail(UpdateMailDTO dto) {
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

        user.setEmail(dto.getNewEmail());
        userRepository.save(user);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO dto) {
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

        user.setMotDePasse(passwordEncoder.encode(dto.getNewMotDePasse()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void anonymiserUtilisateur(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

        user.setEmail("deleted_" + userId + "@example.com");
        user.setMotDePasse("deleted");
        userRepository.save(user);
    }
}
