package org.example.service;

import org.example.model.CacheEntry;
import org.example.util.CleanerThread;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryCache<K, V> {
    private final int maxSize;
    private final long defaultTTL;
    private final Map<K, CacheEntry<V>> cache;
    private final Map<K, LRUNode<K>> nodeMap;
    private final DoublyLinkedList<K> lru;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final CacheStats stats = new CacheStats();

    public InMemoryCache(int maxSize, long defaultTTL) {
        this.maxSize = maxSize;
        this.defaultTTL = defaultTTL;
        this.cache = new HashMap<>();
        this.nodeMap = new HashMap<>();
        this.lru = new DoublyLinkedList<>();
        new CleanerThread<>(this).start();
    }

    public void put(K key, V value) { put(key, value, defaultTTL); }

    public void put(K key, V value, long ttlMillis) {
        lock.writeLock().lock();
        try {
            if (cache.containsKey(key)) {
                lru.moveToFront(nodeMap.get(key));
            } else {
                LRUNode<K> node = lru.addToFront(key);
                nodeMap.put(key, node);
            }
            cache.put(key, new CacheEntry<>(value, ttlMillis));
            if (cache.size() > maxSize) {
                LRUNode<K> removed = lru.removeTail();
                if (removed != null) {
                    cache.remove(removed.key);
                    nodeMap.remove(removed.key);
                    stats.recordEviction();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public V get(K key) {
        lock.writeLock().lock();
        try {
            CacheEntry<V> entry = cache.get(key);
            if (entry == null) {
                stats.recordMiss();
                return null;
            }
            if (entry.isExpired()) {
                cache.remove(key);
                nodeMap.remove(key);
                stats.recordExpiredRemoval();
                stats.recordMiss();
                return null;
            }
            lru.moveToFront(nodeMap.get(key));
            stats.recordHit();
            return entry.getValue();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delete(K key) {
        lock.writeLock().lock();
        try {
            cache.remove(key);
            if (nodeMap.containsKey(key)) {
                lru.remove(nodeMap.get(key));
                nodeMap.remove(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
            nodeMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Map<String, Object> getStats() {
        return stats.getStats();
    }

    public Set<K> getKeySet() {
        return cache.keySet();
    }

    public Map<K, CacheEntry<V>> getCacheMap() {
        return cache;
    }
}

