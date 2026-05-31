package com.gwozdz1uuu.sggwstudentmap.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Osobny łańcuch dla Swagger UI — wyższy priorytet; iframe z Angular dev servera (4200 → 8080).
 */
@Configuration
@RequiredArgsConstructor
public class SwaggerUiSecurityChainConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain swaggerUiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**")
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> c.anyRequest().permitAll())
                .headers(h -> h
                        .frameOptions(frame -> frame.disable())
                        .contentTypeOptions(cto -> {})
                        .referrerPolicy(rp -> rp.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .permissionsPolicyHeader(pp -> pp.policy(
                                "geolocation=(), microphone=(), camera=()")));
        return http.build();
    }
}
