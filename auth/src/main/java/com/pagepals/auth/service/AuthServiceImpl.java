package com.pagepals.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pagepals.auth.dto.AuthResponseDTO;
import com.pagepals.auth.dto.LoginDTO;
import com.pagepals.auth.dto.RegisterDTO;
import com.pagepals.auth.exception.CustomExceptionHandler;
import com.pagepals.auth.exception.EmailAlreadyUsedException;
import com.pagepals.auth.exception.InvalidCredentialException;
import com.pagepals.auth.exception.PseudoAlreadyUsedException;
import com.pagepals.auth.jwt.JWTGenerator;
import com.pagepals.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.pagepals.auth.model.Role;
import com.pagepals.auth.model.UserEntity;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTGenerator jwtGenerator;

    @Override
    public AuthResponseDTO register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException("Cet email est déjà utilisé !");
        }

        if (userRepository.existsByPseudo(dto.getPseudo())) {
            throw new PseudoAlreadyUsedException("Ce pseudo est déjà utilisé !");
        }

        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        user.setPseudo(dto.getPseudo());
        user.setRole(Role.MEMBRE);

        userRepository.save(user);

        String token = jwtGenerator.generateToken(user.getEmail());

        return new AuthResponseDTO(token, user.getEmail(), user.getPseudo(), user.getRole().name());
    }

    @Override
    public AuthResponseDTO login(LoginDTO dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialException("Identifiants incorrects"));

        if (!passwordEncoder.matches(dto.getMotDePasse(), user.getMotDePasse())) {
            throw new InvalidCredentialException("Identifiants incorrects");
        }

        String token = jwtGenerator.generateToken(user.getEmail());

        return new AuthResponseDTO(token, user.getEmail(), user.getPseudo(), user.getRole().name());
    }

}
