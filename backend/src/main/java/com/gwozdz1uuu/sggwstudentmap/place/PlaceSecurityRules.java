package com.gwozdz1uuu.sggwstudentmap.place;

import com.gwozdz1uuu.sggwstudentmap.auth.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class PlaceSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(HttpMethod.GET, "/api/places", "/api/places/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/places", "/api/places/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/places").authenticated() //.hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/places/**").authenticated() //.hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/places/**").authenticated() //.hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/places/pending").authenticated() //.hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/places/pending/*/approve").authenticated() //.hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/places/pending/*/reject").authenticated(); //.hasRole("ADMIN")
        // gdy role będą gotowe: zamień authenticated() na hasRole("ADMIN")
    }
}