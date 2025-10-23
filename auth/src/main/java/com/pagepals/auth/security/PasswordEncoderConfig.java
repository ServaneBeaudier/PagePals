package com.pagepals.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration de sécurité responsable de la création du bean {@link PasswordEncoder}.
 * 
 * Fournit un encodeur BCrypt pour le hachage sécurisé des mots de passe utilisateurs.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Déclare le bean {@link PasswordEncoder} utilisé par l'application
     * pour chiffrer et vérifier les mots de passe.
     *
     * @return une instance de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
