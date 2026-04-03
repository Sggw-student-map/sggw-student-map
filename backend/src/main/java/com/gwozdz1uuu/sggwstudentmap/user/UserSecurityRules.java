package com.gwozdz1uuu.sggwstudentmap.user;

import com.gwozdz1uuu.sggwstudentmap.auth.common.SecurityRules;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Component
public class UserSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(HttpMethod.POST, "/users").permitAll();

    }
}
