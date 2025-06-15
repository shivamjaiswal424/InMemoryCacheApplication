package org.example.util;

import org.example.model.CacheEntry;
import org.example.service.InMemoryCache;

public class CleanerThread<K, V> extends Thread {
    private final InMemoryCache<K, V> cache;

    public CleanerThread(InMemoryCache<K, V> cache) {
        this.cache = cache;
        setDaemon(true);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
                for (K key : cache.getKeySet()) {
                    CacheEntry<V> entry = cache.getCacheMap().get(key);
                    if (entry != null && entry.isExpired()) {
                        cache.delete(key);
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
