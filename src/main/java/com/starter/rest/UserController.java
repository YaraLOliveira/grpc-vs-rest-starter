package com.starter.rest;

import com.starter.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * REST Controller - Traditional HTTP/1.1 + JSON
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    // In-memory database
    private final Map<Long, User> database = new ConcurrentHashMap<>();
    private long nextId = 1;

    public UserController() {
        // Seed with sample data
        for (int i = 1; i <= 10; i++) {
            User user = new User(
                    (long) i,
                    "User " + i,
                    "user" + i + "@example.com",
                    20 + i
            );
            database.put((long) i, user);
        }
        nextId = 11;

        System.out.println("✅ REST Controller initialized with " + database.size() + " users");
    }

    /**
     * GET /api/users/{id}
     * Simple Request-Response with JSON
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        System.out.println("🔵 REST GET /api/users/" + id);

        User user = database.get(id);
        if (user == null) {
            throw new RuntimeException("User not found: " + id);
        }
        return user;
    }

    /**
     * GET /api/users?limit=10
     * Returns complete list at once
     */
    @GetMapping
    public List<User> listUsers(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        System.out.println("🔵 REST GET /api/users?limit=" + limit);

        return database.values().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * POST /api/users
     * Create new user
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        System.out.println("🔵 REST POST /api/users - Creating: " + user.getName());

        user.setId(nextId++);
        database.put(user.getId(), user);
        return user;
    }

    /**
     * PUT /api/users/{id}
     * Update existing user
     */
    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        System.out.println("🔵 REST PUT /api/users/" + id);

        User existing = database.get(id);
        if (existing == null) {
            throw new RuntimeException("User not found: " + id);
        }

        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setAge(user.getAge());

        return existing;
    }

    /**
     * DELETE /api/users/{id}
     * Remove user
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        System.out.println("🔵 REST DELETE /api/users/" + id);

        if (database.remove(id) == null) {
            throw new RuntimeException("User not found: " + id);
        }
    }

    /**
     * GET /api/users/health
     * Health check
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "REST",
                "protocol", "HTTP/1.1",
                "serialization", "JSON",
                "users_count", String.valueOf(database.size())
        );
    }
}