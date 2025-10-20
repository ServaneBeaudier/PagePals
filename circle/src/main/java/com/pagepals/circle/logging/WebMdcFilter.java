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

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            if (request instanceof HttpServletRequest req) {
                // Ajoute les infos HTTP dans le MDC
                MDC.put("http.path", req.getRequestURI());
                MDC.put("http.method", req.getMethod());
                MDC.put("http.remote", 
                        Optional.ofNullable(req.getHeader("X-Forwarded-For"))
                                .orElse(req.getRemoteAddr()));
                // Génère un traceId si absent
                MDC.put("traceId", UUID.randomUUID().toString());
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // Nettoie à la fin pour éviter la fuite entre threads
        }
    }
}
