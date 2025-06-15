package org.example.controller;

import org.example.service.InMemoryCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final InMemoryCache<String, String> cache = new InMemoryCache<>(1000, 300_000); // 300_000 ms = 5 minutes

    // PUT: Add an item to the cache with optional TTL
    @PostMapping("/put")
    public ResponseEntity<String> put(
            @RequestParam("key") String key,
            @RequestParam("value") String value,
            @RequestParam(name = "ttl", required = false) Long ttl) {
        if (ttl != null) {
            cache.put(key, value, ttl);
        } else {
            cache.put(key, value);
        }
        return ResponseEntity.ok("Item added to cache");
    }

    // GET: Retrieve an item from the cache
    @GetMapping("/get")
    public ResponseEntity<String> getCacheValue(@RequestParam("key") String key) {
        String result = cache.get(key);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Key not found in cache");

        }
        return ResponseEntity.ok(result);
    }

    // DELETE: Remove an item by key
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("key") String key) {
        cache.delete(key);
        return ResponseEntity.ok("Item deleted from cache");
    }

    // CLEAR: Remove all items
    @PostMapping("/clear")
    public ResponseEntity<String> clear() {
        cache.clear();
        return ResponseEntity.ok("Cache cleared");
    }

    // STATS: Retrieve cache stats
    @GetMapping("/stats")
    public ResponseEntity<Object> stats() {
        return ResponseEntity.ok(cache.getStats());
    }
}
