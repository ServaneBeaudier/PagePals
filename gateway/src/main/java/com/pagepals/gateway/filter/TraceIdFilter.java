package com.pagepals.gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Vérifie si un traceId est déjà présent (propagation)
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        // Ajoute le traceId dans le MDC (pour les logs locaux)
        MDC.put("traceId", traceId);

        // Ajoute le traceId dans les headers sortants
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(TRACE_ID_HEADER, traceId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doFinally(signalType -> MDC.clear());
    }

    @Override
    public int getOrder() {
        // Très haut dans la chaîne (avant les filtres de routage)
        return -100;
    }
}
