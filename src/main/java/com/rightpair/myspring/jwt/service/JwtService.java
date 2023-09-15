package com.rightpair.myspring.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.KeyException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtService {
  private static final String BEARER_TOKEN_PREFIX = "Bearer ";

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.expire.access}")
  private String ACCESS_EXPIRE_TIME;

  @Value("${jwt.expire.refresh}")
  private String REFRESH_EXPIRE_TIME;

  public String createAccessToken(String subject, long currentTime) {
    long expiredTime = Long.parseLong(REFRESH_EXPIRE_TIME);
    Date expiration = new Date(currentTime + expiredTime);

    try {
      Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

      return BEARER_TOKEN_PREFIX + Jwts.builder()
          .setSubject(subject)
          .setExpiration(expiration)
          .signWith(key, SignatureAlgorithm.HS512)
          .compact();
    } catch (KeyException e) {
      throw new RuntimeException(e);
    }
  }

  public Claims verifyToken(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
          .build().parseClaimsJws(token).getBody();
    } catch (JwtException e) {
      throw new RuntimeException(e);
    }
  }
}
