package com.pagepals.circle.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité Spring Security pour le microservice circle-service.
 * 
 * Ce service étant utilisé principalement par d'autres microservices via l'API Gateway,
 * toutes les requêtes sont autorisées et la gestion de session est désactivée (stateless).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Définit la configuration de la chaîne de filtres de sécurité.
     *
     * @param http objet HttpSecurity à configurer
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
