package org.example;

import org.example.service.InMemoryCache;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InMemoryCacheTest {

    private InMemoryCache<String, String> cache;

    @BeforeEach
    public void setup() {
        cache = new InMemoryCache<>(5, 5000); // small size for eviction test
    }

    @Test
    @Order(1)
    public void testPutAndGet() {
        cache.put("key1", "value1");
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    @Order(2)
    public void testExpiration() throws InterruptedException {
        cache.put("temp", "data", 1000); // 1 second TTL
        Thread.sleep(1500);
        assertNull(cache.get("temp"));
    }

    @Test
    @Order(3)
    public void testEviction() {
        for (int i = 0; i < 10; i++) {
            cache.put("key" + i, "value" + i);
        }
        assertNull(cache.get("key0")); // Evicted
        assertEquals("value9", cache.get("key9")); // Should exist
    }

    @Test
    @Order(4)
    public void testDelete() {
        cache.put("key", "value");
        cache.delete("key");
        assertNull(cache.get("key"));
    }

    @Test
    @Order(5)
    public void testBasicTTLUsage() {
        cache.put("config:db_host", "localhost:5432");
        cache.put("config:api_key", "abc123", 60000L);
        assertEquals("localhost:5432", cache.get("config:db_host"));
        assertEquals("abc123", cache.get("config:api_key"));
    }

    @Test
    @Order(6)
    public void testMaxSizeEvictionBehavior() {
        InMemoryCache<String, String> bigCache = new InMemoryCache<>(1000, 5000);
        for (int i = 0; i < 1200; i++) {
            bigCache.put("data:" + i, "value_" + i);
        }
        assertNull(bigCache.get("data:0")); // Should be evicted
        assertEquals("value_1199", bigCache.get("data:1199"));
    }

    @Test
    @Order(7)
    public void testConcurrentAccess() throws InterruptedException {
        InMemoryCache<String, String> concurrentCache = new InMemoryCache<>(1000, 5000);

        Runnable worker = () -> {
            for (int i = 0; i < 100; i++) {
                String key = Thread.currentThread().getName() + ":item_" + i;
                concurrentCache.put(key, "data_" + i);
                concurrentCache.get(key);
                concurrentCache.get(key.replace("item_" + i, "item_" + (i / 2)));
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.submit(worker);
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        int totalRequests = (int) concurrentCache.getStats().get("total_requests");
        assertTrue(totalRequests > 0);
    }

    @Test
    @Order(8)
    public void testCacheStats() {
        cache.put("alpha", "one");
        cache.get("alpha"); // hit
        cache.get("beta");  // miss
        var stats = cache.getStats();

        assertEquals(2, stats.get("total_requests"));
        assertEquals(1, stats.get("hits"));
        assertEquals(1, stats.get("misses"));
        assertEquals(0, stats.get("evictions")); // Depending on order
    }
}
