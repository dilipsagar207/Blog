package com.example.blog.config;

import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  public OAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
    String username = oauthUser.getAttribute("email");
    if (username == null) username = oauthUser.getName();

    var userOpt = userRepository.findByUsername(username);
    if (userOpt.isEmpty()) {
      User u = new User();
      u.setUsername(username);
      u.setEmail(oauthUser.getAttribute("email"));
      u.setPassword("OAUTH2_USER");
      u.setRoles(Set.of(Role.ROLE_USER));
      userRepository.save(u);
    }
    String token = jwtUtil.generateToken(username);
    response.sendRedirect("/oauth2-success?token=" + token);
  }
}
