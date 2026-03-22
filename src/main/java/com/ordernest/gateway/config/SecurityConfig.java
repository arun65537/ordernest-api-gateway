package com.ordernest.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/api/auth/**",
            "/auth/**",
            "/.well-known/**"
    };

    // ✅ 1. PUBLIC ENDPOINTS (NO JWT)
    @Bean
    @Order(1)
    public SecurityWebFilterChain publicChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(
                        ServerWebExchangeMatchers.pathMatchers(PUBLIC_PATHS)
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .anyExchange().permitAll()
                )
                .build();  // ← No oauth2ResourceServer here — no JWT filter at all
    }

    // ✅ 2. SECURED ENDPOINTS (JWT REQUIRED)
    @Bean
    @Order(2)
    public SecurityWebFilterChain securedChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(
                        ServerWebExchangeMatchers.pathMatchers(
                                "/api/**",   // all other API routes
                                "/actuator/**"
                        )
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/shipments/status").hasRole("ADMIN")
                        .anyExchange().authenticated()  // ← was permitAll, should be authenticated
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                .build();
    }
}