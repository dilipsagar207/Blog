package com.example.blog.controller;

import com.example.blog.entity.Post;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  public PostController(PostRepository postRepository, UserRepository userRepository) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
  }

  @GetMapping
  public List<Post> list() { return postRepository.findAll(); }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody Post p, Authentication auth) {
    if (auth == null) return ResponseEntity.status(401).body("unauthenticated");
    String username = auth.getName();
    var uOpt = userRepository.findByUsername(username);
    if (uOpt.isEmpty()) return ResponseEntity.status(403).body("user not found");
    p.setAuthor(uOpt.get());
    var saved = postRepository.save(p);
    return ResponseEntity.ok(saved);
  }
}
