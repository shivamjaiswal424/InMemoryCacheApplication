package org.example.service;

import org.springframework.stereotype.Service;

import java.util.Map;

public class CacheStats {
    private int hits = 0, misses = 0, evictions = 0, expiredRemovals = 0, totalRequests = 0;
    private int currentSize = 0;
    public synchronized void setCurrentSize(int size) {
        this.currentSize = size;
    }
    public synchronized void recordHit() { hits++; totalRequests++; }
    public synchronized void recordMiss() { misses++; totalRequests++; }
    public synchronized void recordEviction() { evictions++; }
    public synchronized void recordExpiredRemoval() { expiredRemovals++; }

    public synchronized Map<String, Object> getStats() {
        return Map.of(
                "hits", hits,
                "misses", misses,
                "hit_rate", totalRequests == 0 ? 0.0 : (double) hits / totalRequests,
                "total_requests", totalRequests,
                "current_size", currentSize,
                "evictions", evictions,
                "expired_removals", expiredRemovals
        );
    }
}
