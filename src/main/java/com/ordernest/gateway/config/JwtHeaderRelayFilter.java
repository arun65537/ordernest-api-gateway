package com.ordernest.gateway.config;

import java.util.List;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtHeaderRelayFilter implements GlobalFilter, Ordered {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLES_HEADER = "X-User-Roles";

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .ofType(JwtAuthenticationToken.class)
                .map(jwtAuthenticationToken -> {
                    String userId = jwtAuthenticationToken.getToken().getClaimAsString("userId");
                    String email = jwtAuthenticationToken.getToken().getClaimAsString("email");
                    List<String> roles = jwtAuthenticationToken.getToken().getClaimAsStringList("roles");
                    ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();

                    if (userId != null && !userId.isBlank()) {
                        requestBuilder.header(USER_ID_HEADER, userId);
                    }
                    if (email != null && !email.isBlank()) {
                        requestBuilder.header(USER_EMAIL_HEADER, email);
                    }
                    if (roles != null && !roles.isEmpty()) {
                        requestBuilder.header(USER_ROLES_HEADER, String.join(",", roles));
                    }

                    return exchange.mutate().request(requestBuilder.build()).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
