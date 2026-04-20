package com.gwozdz1uuu.sggwstudentmap.event;

import com.gwozdz1uuu.sggwstudentmap.auth.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class EventSecurityRules implements SecurityRules {

    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
            .requestMatchers(HttpMethod.OPTIONS, "/api/events", "/api/events/**").permitAll()
            .requestMatchers(HttpMethod.GET,    "/api/events", "/api/events/**").authenticated()
            .requestMatchers(HttpMethod.POST,   "/api/events", "/api/events/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/events", "/api/events/**").authenticated();
    }
}