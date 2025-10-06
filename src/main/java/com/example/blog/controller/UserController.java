package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) { this.userRepository = userRepository; }

  @GetMapping("/me")
  public ResponseEntity<?> me(Authentication auth) {
    if (auth == null) return ResponseEntity.status(401).body("unauthenticated");
    String username = auth.getName();
    var u = userRepository.findByUsername(username).orElse(null);
    if (u == null) return ResponseEntity.status(404).body("not found");
    u.setPassword(null);
    return ResponseEntity.ok(u);
  }
}
