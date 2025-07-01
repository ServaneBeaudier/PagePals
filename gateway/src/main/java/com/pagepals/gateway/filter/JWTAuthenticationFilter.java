package com.pagepals.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.HttpStatus;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JWTAuthenticationFilter implements GlobalFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();
    String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

    System.out.println("==> Requête reçue : " + path);
    System.out.println("==> Header Authorization : " + authHeader);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        if (path.startsWith("/api/auth") || path.startsWith("/api/search")
                || path.equals("/api/circles/search")) {
            System.out.println("==> Route publique détectée");
            return chain.filter(exchange);
        }
        System.out.println("==> Accès refusé (pas de token)");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    try {
        String token = authHeader.substring(7);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String userId = String.valueOf(claims.get("userId"));
        String role = claims.get("role", String.class);

        System.out.println("==> Token JWT OK");
        System.out.println("==> userId = " + userId + ", role = " + role);

        exchange = exchange.mutate()
                .request(r -> r.headers(headers -> {
                    headers.add("X-User-Id", userId);
                    headers.add("X-User-Role", role);
                }))
                .build();

    } catch (Exception e) {
        System.out.println("==> ERREUR JWT : " + e.getMessage());
        e.printStackTrace();
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    return chain.filter(exchange);
}


}
