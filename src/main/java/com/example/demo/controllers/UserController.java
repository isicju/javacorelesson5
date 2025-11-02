package com.example.demo.controllers;

import com.example.demo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final List<User> users = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    public UserController() {
        users.add(new User(counter.incrementAndGet(), "Alice", "alice@example.com"));
        users.add(new User(counter.incrementAndGet(), "Bob", "bob@example.com"));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(users);
    }

    @GetMapping("/basic")
    public ResponseEntity<String> getUsers() {
        String jsonContent = "[" +
                "{\"id\": 1, \"name\": \"Alice\"}," +
                "{\"id\": 2, \"name\": \"Bob\"}," +
                "{\"id\": 3, \"name\": \"Charlie\"}" +
                "]";

        return ResponseEntity.ok(jsonContent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = users.stream().filter(u -> u.getId().equals(id)).findFirst();
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> matchingUsers = users.stream()
                .filter(u -> u.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
        return ResponseEntity.ok(matchingUsers);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setId(counter.incrementAndGet());
        users.add(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/users/" + user.getId())
                .body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> existingUser = users.stream().filter(u -> u.getId().equals(id)).findFirst();
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{id}/details/{field}")
    public ResponseEntity<String> getUserField(@PathVariable Long id, @PathVariable String field) {
        Optional<User> user = users.stream().filter(u -> u.getId().equals(id)).findFirst();
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return switch (field.toLowerCase()) {
            case "name" -> ResponseEntity.ok(user.get().getName());
            case "email" -> ResponseEntity.ok(user.get().getEmail());
            default -> ResponseEntity.badRequest().body("Invalid field: " + field);
        };
    }

    @GetMapping("/bylastname")
    public ResponseEntity<List<User>> getUsersByLastName(@RequestParam(required = false) String lastname) {
        List<User> filteredUsers = users;
        if (lastname != null && !lastname.isEmpty()) {
            filteredUsers = users.stream()
                    .filter(user -> user.getName().toLowerCase().contains(lastname.toLowerCase()))
                    .toList();
        }
        return ResponseEntity.ok(filteredUsers);
    }

    @PostMapping(value = "/bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBulkUsers(@RequestBody List<User> newUsers,
                                                 @RequestParam(defaultValue = "false") boolean dryRun) {
        if (dryRun) {
            return ResponseEntity.ok("Dry run: " + newUsers.size() + " users would be created");
        }
        newUsers.forEach(user -> {
            user.setId(counter.incrementAndGet());
            users.add(user);
        });
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newUsers.size() + " users created");
    }

    @PostMapping(value = "/{id}/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMultipleFiles(
            @PathVariable Long id,
            @RequestPart("files") List<MultipartFile> files) {
        Optional<User> user = users.stream().filter(u -> u.getId().equals(id)).findFirst();
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("No files provided");
        }
        try {
            int uploadedCount = 0;
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = "multi_" + id + "_" + file.getOriginalFilename();
                    Path path = Paths.get(System.getProperty("java.io.tmpdir"), fileName);
                    Files.write(path, file.getBytes());
                    uploadedCount++;
                }
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(uploadedCount + " files uploaded for user " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload files: " + e.getMessage());
        }
    }

}