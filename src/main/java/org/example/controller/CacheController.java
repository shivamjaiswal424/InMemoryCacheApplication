package org.example.controller;

import org.example.service.InMemoryCache;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final InMemoryCache<String, String> cache = new InMemoryCache<>(1000, 300_000);

    @PostMapping("/put")
    public void put(@RequestParam String key, @RequestParam String value, @RequestParam(required = false) Long ttl) {
        if (ttl != null) cache.put(key, value, ttl);
        else cache.put(key, value);
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return cache.get(key);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam String key) {
        cache.delete(key);
    }

    @PostMapping("/clear")
    public void clear() {
        cache.clear();
    }

    @GetMapping("/stats")
    public Object stats() {
        return cache.getStats();
    }
}
