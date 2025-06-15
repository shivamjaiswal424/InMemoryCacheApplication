# In-Memory Cache (Thread-Safe)

## Overview
This project implements a high-performance, thread-safe in-memory cache system with support for:
- TTL (time-to-live) based expiration
- LRU (Least Recently Used) eviction
- Cache statistics
- RESTful access

## Technologies Used
- Java 17
- Spring Boot
- JUnit 5

## How to Run
```bash
mvn spring-boot:run
```
### PUT an item into cache with TTL (500000 ms)
POST http://localhost:8080/cache/put?key=fo&value=bar&ttl=500000
####  Response: 
```bash
Item added to cache
```

### GET the item from cache
GET http://localhost:8080/cache/get?key=fo
#### Response:
```bash
bar
```

### View cache statistics
GET http://localhost:8080/cache/stats
#### Response:
```bash
{
"current_size": 1,
"expired_removals": 0,
"hit_rate": 1.0,
"total_requests": 1,
"hits": 1,
"evictions": 0,
"misses": 0
}
```

### DELETE the item from cache
GET http://localhost:8080/cache/delete?key=fo
#### Response: 
```Item deleted from cache```
## API Endpoints
- `POST /cache/put?key=...&value=...&ttl=...`
- `GET /cache/get?key=...`
- `DELETE /cache/delete?key=...`
- `POST /cache/clear`
- `GET /cache/stats`

## Sample curl
```bash
curl -X POST "localhost:8080/cache/put?key=foo&value=bar&ttl=500000"
curl "localhost:8080/cache/get?key=foo"
curl -X DELETE "localhost:8080/cache/delete?key=foo"
curl -X POST "localhost:8080/cache/clear"
curl "localhost:8080/cache/stats"
```

## Design Decisions
- `HashMap` for O(1) lookup
- `DoublyLinkedList` for LRU tracking
- `ReentrantReadWriteLock` for concurrency
- Cleaner thread for expired entry removal

## Performance Considerations
- Background cleanup avoids bloating
- Lock granularity minimized
- Evictions and stats maintained in sync
