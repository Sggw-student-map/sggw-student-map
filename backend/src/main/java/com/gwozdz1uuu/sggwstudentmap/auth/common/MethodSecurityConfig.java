package com.gwozdz1uuu.sggwstudentmap.auth.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// Wlaczenie @PreAuthorize / @PostAuthorize jako defense-in-depth obok regul URL.
// Reguly URL nadal sa pierwszym filtrem; adnotacje metodowe sa drugim, niezaleznym
// punktem kontroli (np. gdy ktos doda nowy endpoint i zapomni o regule URL).
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
