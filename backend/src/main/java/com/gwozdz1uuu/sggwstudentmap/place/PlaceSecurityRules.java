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
                // odczyt publiczny - mapa ma dzialac dla anonimow
                .requestMatchers(HttpMethod.GET, "/api/places", "/api/places/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/places", "/api/places/**").permitAll()
                // tworzenie i usuwanie miejsc - tylko ADMIN
                .requestMatchers(HttpMethod.POST, "/api/places").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/places/**").hasRole("ADMIN")
                // edycja miejsc - ADMIN lub APPROVER
                .requestMatchers(HttpMethod.PUT, "/api/places/**").hasAnyRole("ADMIN", "APPROVER");
    }
}
