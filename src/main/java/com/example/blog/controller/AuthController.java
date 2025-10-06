package com.example.blog.controller;

import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    if (userRepository.findByUsername(req.getUsername()).isPresent()) {
      return ResponseEntity.badRequest().body("username exists");
    }
    User u = new User();
    u.setUsername(req.getUsername());
    u.setPassword(encoder.encode(req.getPassword()));
    u.setEmail(req.getEmail());
    u.setRoles(java.util.Set.of(com.example.blog.entity.Role.ROLE_USER));
    userRepository.save(u);
    return ResponseEntity.ok("registered");
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    var opt = userRepository.findByUsername(req.getUsername());
    if (opt.isEmpty()) return ResponseEntity.status(401).body("invalid");
    User u = opt.get();
    if (!encoder.matches(req.getPassword(), u.getPassword())) {
      return ResponseEntity.status(401).body("invalid");
    }
    String token = jwtUtil.generateToken(u.getUsername());
    return ResponseEntity.ok(java.util.Map.of("token", token));
  }
}
