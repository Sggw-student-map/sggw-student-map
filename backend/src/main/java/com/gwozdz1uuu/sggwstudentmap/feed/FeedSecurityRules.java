package com.gwozdz1uuu.sggwstudentmap.feed;

import com.gwozdz1uuu.sggwstudentmap.auth.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class FeedSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(HttpMethod.OPTIONS, "/api/feed", "/api/feed/**").permitAll()
                .requestMatchers(HttpMethod.GET,     "/api/feed", "/api/feed/**").permitAll()
                .requestMatchers(HttpMethod.POST,    "/api/feed", "/api/feed/**").authenticated()
                .requestMatchers(HttpMethod.PUT,     "/api/feed", "/api/feed/**").authenticated()
                .requestMatchers(HttpMethod.DELETE,  "/api/feed", "/api/feed/**").authenticated();
    }
}
