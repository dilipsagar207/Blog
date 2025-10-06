package com.example.blog.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Post {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String body;

  private Instant createdAt = Instant.now();

  @ManyToOne
  private User author;

  public Post() {}

  public Post(String title, String body, User author) {
    this.title = title;
    this.body = body;
    this.author = author;
    this.createdAt = Instant.now();
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getBody() { return body; }
  public void setBody(String body) { this.body = body; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public User getAuthor() { return author; }
  public void setAuthor(User author) { this.author = author; }
}
