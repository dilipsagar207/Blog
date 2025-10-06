package com.example.blog.service;

import com.example.blog.entity.User;

public interface UserService {
  User register(String username, String password, String email);
}
