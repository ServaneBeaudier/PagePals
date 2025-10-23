package com.pagepals.auth.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Filtre servlet plaçant des informations de contexte HTTP dans le MDC (Mapped Diagnostic Context)
 * afin d'enrichir automatiquement les logs.
 *
 * Ce filtre capture notamment :
 *   Le chemin et la méthode HTTP de la requête
 *   L’adresse IP du client (ou l’en-tête X-Forwarded-For si présent)
 *   Un identifiant unique de trace (traceId) pour corréler les logs inter-services
 *
 * Le MDC est nettoyé après chaque requête pour éviter les fuites de données entre threads.
 *
 * L’ordre le plus élevé garantit son exécution avant tous les autres filtres.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMdcFilter implements Filter {

    /**
     * Intercepte chaque requête HTTP pour ajouter les métadonnées pertinentes
     * dans le MDC (utilisé par SLF4J / Logback pour enrichir les logs).
     *
     * @param request  la requête entrante
     * @param response la réponse à envoyer
     * @param chain    la chaîne de filtres à poursuivre
     * @throws IOException      en cas d’erreur d’E/S
     * @throws ServletException en cas d’erreur de traitement de la requête
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            if (request instanceof HttpServletRequest req) {
                MDC.put("http.path", req.getRequestURI());
                MDC.put("http.method", req.getMethod());
                MDC.put("http.remote",
                        Optional.ofNullable(req.getHeader("X-Forwarded-For"))
                                .orElse(req.getRemoteAddr()));
                MDC.put("traceId", UUID.randomUUID().toString());
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // Nettoyage obligatoire pour éviter la contamination des threads
        }
    }
}
