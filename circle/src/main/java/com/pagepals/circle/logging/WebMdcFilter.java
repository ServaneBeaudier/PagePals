package com.pagepals.circle.logging;

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
 * Filtre HTTP permettant d’enrichir les logs avec des informations de contexte.
 * 
 * Ce filtre ajoute au MDC (Mapped Diagnostic Context) plusieurs métadonnées liées
 * à la requête HTTP courante afin d’améliorer la traçabilité inter-services.
 * 
 * Informations ajoutées :
 * - Chemin de la requête (http.path)
 * - Méthode HTTP (http.method)
 * - Adresse IP du client (http.remote)
 * - Identifiant unique de traçage (traceId)
 * 
 * Le traceId est généré aléatoirement pour chaque requête et propagé dans les logs
 * afin de pouvoir suivre un appel à travers plusieurs microservices.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMdcFilter implements Filter {

    /**
     * Ajoute les informations de requête HTTP dans le MDC avant le traitement.
     * Nettoie le contexte à la fin pour éviter les fuites entre threads.
     *
     * @param request  requête HTTP entrante
     * @param response réponse HTTP sortante
     * @param chain    chaîne de filtres à exécuter
     * @throws IOException      en cas d’erreur d’entrée/sortie
     * @throws ServletException en cas d’erreur dans la chaîne de filtres
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
            MDC.clear();
        }
    }
}
