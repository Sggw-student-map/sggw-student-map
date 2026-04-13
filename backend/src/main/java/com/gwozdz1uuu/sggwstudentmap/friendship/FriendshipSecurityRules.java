package com.gwozdz1uuu.sggwstudentmap.friendship;

import com.gwozdz1uuu.sggwstudentmap.auth.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class FriendshipSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
            .requestMatchers(HttpMethod.OPTIONS, "/api/friends", "/api/friends/**").permitAll()
            .requestMatchers(HttpMethod.GET,    "/api/friends", "/api/friends/**").authenticated()
            .requestMatchers(HttpMethod.POST,   "/api/friends", "/api/friends/**").authenticated()
            .requestMatchers(HttpMethod.PUT,    "/api/friends", "/api/friends/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/friends", "/api/friends/**").authenticated();
    }
}