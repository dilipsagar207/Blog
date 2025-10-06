package com.example.blog.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

  private final Key key = Keys.hmacShaKeyFor("ReplaceThisWithAStrongSecretKeyOfAtLeast256BitsLength!".getBytes());
  private final long validityMillis = 1000 * 60 * 60 * 24; // 24h

  public String generateToken(String username) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + validityMillis);
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUsername(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }
}
