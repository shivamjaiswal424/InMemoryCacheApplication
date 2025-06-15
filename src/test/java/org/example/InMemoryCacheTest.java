package org.example;

import org.example.service.InMemoryCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InMemoryCacheTest {

    private InMemoryCache<String, String> cache;

    @BeforeEach
    public void setup() {
        cache = new InMemoryCache<>(5, 5000);
    }

    @Test
    public void testPutAndGet() {
        cache.put("key1", "value1");
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    public void testExpiration() throws InterruptedException {
        cache.put("temp", "data", 1000);
        Thread.sleep(1500);
        assertNull(cache.get("temp"));
    }

    @Test
    public void testEviction() {
        for (int i = 0; i < 10; i++) {
            cache.put("key" + i, "value" + i);
        }
        assertNull(cache.get("key0"));
    }

    @Test
    public void testDelete() {
        cache.put("key", "value");
        cache.delete("key");
        assertNull(cache.get("key"));
    }
}