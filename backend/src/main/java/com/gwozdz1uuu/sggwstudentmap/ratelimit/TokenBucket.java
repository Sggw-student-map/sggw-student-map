package com.gwozdz1uuu.sggwstudentmap.ratelimit;

import java.time.Duration;

/**
 * Thread-safe token bucket rate limiter.
 * Tokens refill at a steady rate up to a maximum capacity.
 */
class TokenBucket {

    private final long capacity;
    private final long refillIntervalNanos;
    private long availableTokens;
    private long lastRefillNanos;
    private volatile long lastAccessNanos;

    TokenBucket(long capacity, Duration refillPeriod) {
        this.capacity = capacity;
        this.refillIntervalNanos = refillPeriod.toNanos() / capacity;
        this.availableTokens = capacity;
        long now = System.nanoTime();
        this.lastRefillNanos = now;
        this.lastAccessNanos = now;
    }

    synchronized boolean tryConsume() {
        refill();
        lastAccessNanos = System.nanoTime();
        if (availableTokens > 0) {
            availableTokens--;
            return true;
        }
        return false;
    }

    synchronized long remainingTokens() {
        refill();
        return availableTokens;
    }

    synchronized long nanosToNextToken() {
        refill();
        if (availableTokens > 0) return 0;
        return refillIntervalNanos - (System.nanoTime() - lastRefillNanos);
    }

    boolean isStale(long maxIdleNanos) {
        return System.nanoTime() - lastAccessNanos > maxIdleNanos;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsed = now - lastRefillNanos;
        long tokensToAdd = elapsed / refillIntervalNanos;
        if (tokensToAdd > 0) {
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillNanos += tokensToAdd * refillIntervalNanos;
        }
    }
}
