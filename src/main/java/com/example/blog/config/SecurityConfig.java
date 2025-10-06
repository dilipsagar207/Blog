package com.example.blog.config;

import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final AuthenticationSuccessHandler oauthSuccessHandler;

  public SecurityConfig(JwtUtil jwtUtil, UserRepository userRepository, AuthenticationSuccessHandler oauthSuccessHandler) {
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.oauthSuccessHandler = oauthSuccessHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/oauth2/**", "/oauth2-success").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth -> oauth
            .successHandler(oauthSuccessHandler)
        )
        .httpBasic(Customizer.withDefaults());

    http.addFilterBefore(new JwtFilter(jwtUtil, userRepository), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationSuccessHandler oauth2SuccessHandler(UserRepository ur, JwtUtil jwtUtil) {
    return new OAuth2SuccessHandler(ur, jwtUtil);
  }

  public static class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
      this.jwtUtil = jwtUtil;
      this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
      String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (auth != null && auth.startsWith("Bearer ")) {
        String token = auth.substring(7);
        if (jwtUtil.validateToken(token)) {
          String username = jwtUtil.getUsername(token);
          var userOpt = userRepository.findByUsername(username);
          if (userOpt.isPresent()) {
            var user = userOpt.get();
            var authToken = new UsernamePasswordAuthenticationToken(
                username,
                null,
                user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.name())).collect(Collectors.toList())
            );
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }
      }
      filterChain.doFilter(request, response);
    }
  }
}
