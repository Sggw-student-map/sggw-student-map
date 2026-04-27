package com.gwozdz1uuu.sggwstudentmap.settings;

import com.gwozdz1uuu.sggwstudentmap.auth.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class UserSettingsSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
            .requestMatchers(HttpMethod.OPTIONS, "/api/settings").permitAll()
            .requestMatchers(HttpMethod.GET,     "/api/settings").authenticated()
            .requestMatchers(HttpMethod.PUT,     "/api/settings").authenticated();
    }
}