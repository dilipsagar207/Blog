package com.example.blog;

import com.example.blog.dto.RegisterRequest;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

  @Autowired
  private TestRestTemplate rest;

  @Autowired
  private UserRepository userRepository;

  @Test
  void registerAndLogin() {
    RegisterRequest r = new RegisterRequest();
    r.setUsername("testuser");
    r.setPassword("password");
    r.setEmail("test@example.com");

    ResponseEntity<String> reg = rest.postForEntity("/api/auth/register", r, String.class);
    assertThat(reg.getStatusCode()).isEqualTo(HttpStatus.OK);

    var loginReq = new com.example.blog.dto.LoginRequest();
    loginReq.setUsername("testuser");
    loginReq.setPassword("password");
    ResponseEntity<java.util.Map> login = rest.postForEntity("/api/auth/login", loginReq, java.util.Map.class);
    assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(login.getBody()).containsKey("token");
  }
}
