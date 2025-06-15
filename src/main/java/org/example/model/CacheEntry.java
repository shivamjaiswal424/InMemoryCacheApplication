package org.example.model;

public class CacheEntry<V> {
    private final V value;
    private final long expiryTime;

    public CacheEntry(V value, long ttlMillis) {
        this.value = value;
        this.expiryTime = ttlMillis > 0 ? System.currentTimeMillis() + ttlMillis : -1;
    }

    public boolean isExpired() {
        return expiryTime != -1 && System.currentTimeMillis() > expiryTime;
    }

    public V getValue() {
        return value;
    }
}