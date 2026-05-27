package com.gwozdz1uuu.sggwstudentmap.auth;

import com.gwozdz1uuu.sggwstudentmap.auth.common.SecurityRules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;


@Slf4j
@Component
public class AuthSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(HttpMethod.GET, "/auth/confirm").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/reviews", "/api/reviews/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/reviews", "/api/reviews/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
    }
}



// @Configuration
// @EnableMethodSecurity
// public class MethodSecurityConfig {}