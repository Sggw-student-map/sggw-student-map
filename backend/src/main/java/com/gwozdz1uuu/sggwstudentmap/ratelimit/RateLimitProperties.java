package com.gwozdz1uuu.sggwstudentmap.ratelimit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;

    /** Max requests per minute for regular API endpoints (per IP). */
    private int globalRequestsPerMinute = 60;

    /** Max requests per minute for auth endpoints like /auth/login (per IP). */
    private int authRequestsPerMinute = 10;
}
