package com.gwozdz1uuu.sggwstudentmap.ratelimit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;

    private final ConcurrentHashMap<String, TokenBucket> generalBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TokenBucket> authBuckets = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;

    private static final Duration STALE_THRESHOLD = Duration.ofMinutes(10);

    private static final Set<String> AUTH_PATH_PREFIXES = Set.of(
            "/auth/login",
            "/auth/refresh",
            "/users"
    );

    @PostConstruct
    void startCleanup() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "rate-limit-cleanup");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::evictStaleBuckets, 10, 10, TimeUnit.MINUTES);
    }

    @PreDestroy
    void stopCleanup() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = resolveClientIp(request);
        String path = request.getRequestURI();
        boolean isAuthPath = AUTH_PATH_PREFIXES.stream().anyMatch(path::startsWith);

        TokenBucket bucket = isAuthPath
                ? authBuckets.computeIfAbsent(clientIp,
                    k -> new TokenBucket(properties.getAuthRequestsPerMinute(), Duration.ofMinutes(1)))
                : generalBuckets.computeIfAbsent(clientIp,
                    k -> new TokenBucket(properties.getGlobalRequestsPerMinute(), Duration.ofMinutes(1)));

        int limit = isAuthPath
                ? properties.getAuthRequestsPerMinute()
                : properties.getGlobalRequestsPerMinute();

        if (bucket.tryConsume()) {
            response.setIntHeader("X-RateLimit-Limit", limit);
            response.setHeader("X-RateLimit-Remaining",
                    String.valueOf(bucket.remainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitSeconds = Math.max(1,
                    TimeUnit.NANOSECONDS.toSeconds(bucket.nanosToNextToken()));

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setIntHeader("X-RateLimit-Limit", limit);
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("Retry-After", String.valueOf(waitSeconds));
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":429,"
                    + "\"error\":\"Too Many Requests\","
                    + "\"message\":\"Rate limit exceeded. Try again in "
                    + waitSeconds + " seconds.\"}");

            log.warn("Rate limit exceeded | ip={} path={} method={}",
                    clientIp, path, request.getMethod());
        }
    }

    /**
     * Resolves client IP from X-Forwarded-For (set by Cloud Run / reverse proxy)
     * or falls back to remoteAddr for local development.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void evictStaleBuckets() {
        long threshold = STALE_THRESHOLD.toNanos();
        int beforeGeneral = generalBuckets.size();
        int beforeAuth = authBuckets.size();
        generalBuckets.entrySet().removeIf(e -> e.getValue().isStale(threshold));
        authBuckets.entrySet().removeIf(e -> e.getValue().isStale(threshold));
        log.debug("Rate limit cleanup: general {}->{}, auth {}->{}",
                beforeGeneral, generalBuckets.size(),
                beforeAuth, authBuckets.size());
    }
}
