package com.rightpair.myspring.jwt.service;

import com.rightpair.myspring.jwt.dto.JwtTokenPair;
import com.rightpair.myspring.jwt.dto.RefreshTokenDto;
import com.rightpair.myspring.jwt.entity.JwtEntity;
import com.rightpair.myspring.jwt.exception.JwtDeniedException;
import com.rightpair.myspring.jwt.exception.JwtExpiredException;
import com.rightpair.myspring.jwt.repository.JwtRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.KeyException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtService {
  private static final String BEARER_TOKEN_STRING = "Bearer";

  private final JwtRepository jwtRepository;

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.expire.access}")
  private Long ACCESS_EXPIRE_TIME;

  @Value("${jwt.expire.refresh}")
  private Long REFRESH_EXPIRE_TIME;

  public RefreshTokenDto.Response refreshAccessToken(RefreshTokenDto.Request request) {
    String refreshToken = request.refreshToken();
    Claims verifyToken = verifyToken(refreshToken);

    // Subject가 다르면 Deny
    if (!String.valueOf(request.memberId()).equals(verifyToken.getSubject())) {
      throw new JwtDeniedException();
    }

    JwtTokenPair jwtTokenPair = jwtRepository.findById(request.memberId())
        .map(entity -> {
          // 저장되어 있는 토큰과 다르면 Deny
          if (!entity.refreshToken().equals(refreshToken)) {
            throw new JwtDeniedException();
          }
          return createJwtTokenPairWithRefreshToken(
              verifyToken.getSubject(), refreshToken
          );
        })
        .orElseGet(() -> createJwtTokenPair(request.memberId()));

    return RefreshTokenDto.Response.builder()
        .grantType(BEARER_TOKEN_STRING)
        .accessToken(jwtTokenPair.accessToken())
        .refreshToken(jwtTokenPair.refreshToken())
        .build();
  }

  public JwtTokenPair createJwtTokenPair(Long memberId) {
    return createJwtTokenPair(String.valueOf(memberId));
  }

  public JwtTokenPair createJwtTokenPair(String subject) {
    String refreshToken = createRefreshToken(subject, System.currentTimeMillis());
    return createJwtTokenPairWithRefreshToken(subject, refreshToken);
  }

  public JwtTokenPair createJwtTokenPairWithRefreshToken(String subject, String refreshToken) {
    return JwtTokenPair.builder()
        .accessToken(createAccessToken(subject, System.currentTimeMillis()))
        .refreshToken(refreshToken)
        .build();
  }

  public String createAccessToken(String subject, long currentTime) {
    return createToken(subject, currentTime, ACCESS_EXPIRE_TIME);
  }

  public String createRefreshToken(String subject, long currentTime) {
    String refreshToken = createToken(subject, currentTime, REFRESH_EXPIRE_TIME);
    jwtRepository.save(
        JwtEntity.builder()
            .memberId(Long.parseLong(subject))
            .refreshToken(refreshToken)
            .build()
    );
    return refreshToken;
  }

  public String createToken(String subject, long currentTime, long expiredTime) {
    Date expiration = new Date(currentTime + expiredTime);

    try {
      Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

      return Jwts.builder()
          .setSubject(subject)
          .setIssuedAt(new Date(currentTime))
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
          .setSigningKey(Decoders.BASE64.decode(SECRET_KEY))
          .build().parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      throw new JwtExpiredException();
    } catch (JwtException e) {
      throw new JwtDeniedException();
    }
  }
}
