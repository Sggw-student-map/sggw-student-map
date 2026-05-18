package com.gwozdz1uuu.sggwstudentmap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Dedykowany lancuch Spring Security dla endpointow Actuatora (/actuator/**).
 *
 * Zalozenia bezpieczenstwa:
 *  - management dziala na osobnym porcie (patrz application.properties:
 *    management.server.port=8081, management.server.address=127.0.0.1),
 *    co oznacza ze ten lancuch praktycznie obsluguje tylko ruch z loopbacka
 *    wewnatrz kontenera (Docker HEALTHCHECK / kubelet z tego samego poda),
 *  - faktyczna whitelista endpointow jest po stronie Actuatora
 *    (management.endpoints.web.exposure.include=health) - tylko health
 *    zostaje zarejestrowany w mapping. Wszystkie inne /actuator/* sciezki
 *    nie istnieja po stronie aplikacji i zwracaja 404 z dispatchera,
 *  - chain celowo robi permitAll() zamiast denyAll() na nieznanych /actuator/*:
 *    chcemy 404 (brak informacji) zamiast 401/403 (info-leak: "endpoint
 *    istnieje, tylko nie masz dostepu"). 404 jest neutralne dla atakujacego,
 *  - CSRF wylaczone (stateless, brak ciastek), CORS wylaczony (brak powodu
 *    by przegladarka kiedykolwiek wolala /actuator),
 *  - sztywne naglowki bezpieczenstwa (XCTO, frame-deny, no-store, restrykcyjne
 *    Referrer-Policy) - defense-in-depth na wypadek bledu w konfiguracji portu.
 */
@Configuration
public class ActuatorSecurityChainConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/actuator/**")
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .requestCache(rc -> rc.disable())
                .authorizeHttpRequests(c -> c.anyRequest().permitAll())
                .exceptionHandling(c -> {
                    c.authenticationEntryPoint((request, response, authException) ->
                            response.setStatus(HttpStatus.NOT_FOUND.value()));
                    c.accessDeniedHandler((request, response, deniedException) ->
                            response.setStatus(HttpStatus.NOT_FOUND.value()));
                })
                .headers(h -> h
                        .frameOptions(f -> f.deny())
                        .contentTypeOptions(cto -> {})
                        .cacheControl(cc -> {})
                        .referrerPolicy(rp -> rp.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)));

        return http.build();
    }
}
