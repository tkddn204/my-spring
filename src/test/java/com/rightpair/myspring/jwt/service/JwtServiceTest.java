package com.rightpair.myspring.jwt.service;

import com.rightpair.myspring.jwt.dto.RefreshTokenDto;
import com.rightpair.myspring.jwt.exception.JwtDeniedException;
import com.rightpair.myspring.jwt.exception.JwtExpiredException;
import com.rightpair.myspring.utils.TestSettings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(value = "test")
class JwtServiceTest extends TestSettings {
  private final static String BEARER_TOKEN_PREFIX = "Bearer ";

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Autowired
  private JwtService jwtService;

  @DisplayName("createAccessToken을 호출할 때")
  @Nested
  class AccessTokenTest {

    @DisplayName("액세스토큰을 생성하는 데 성공한다.")
    @Test
    void shouldSuccessCreatingAccessToken() {
      // given
      Long memberId = 1234L;
      String accessToken = jwtService.createAccessToken(
          String.valueOf(memberId), System.currentTimeMillis());

      // when
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(Decoders.BASE64.decode(SECRET_KEY))
          .build().parseClaimsJws(accessToken).getBody();

      // then
      assertEquals(String.valueOf(memberId), claims.getSubject());
    }
  }

  @DisplayName("createRefreshToken을 호출할 때")
  @Nested
  class CreateRefreshToken {

    @DisplayName("리프레시 토큰을 생성하는 데 성공한다.")
    @Test
    void shouldSuccessCreatingAccessToken() {
      // given
      Long memberId = 1234L;
      String refreshToken = jwtService.createRefreshToken(
          String.valueOf(memberId), System.currentTimeMillis());

      // when
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(Decoders.BASE64.decode(SECRET_KEY))
          .build().parseClaimsJws(refreshToken).getBody();

      // then
      assertEquals(String.valueOf(memberId), claims.getSubject());
    }
  }

  @DisplayName("refreshAccessToken을 호출할 때")
  @Nested
  class RefreshAccessTokenTest {


    @DisplayName("액세스 토큰 재생성에 성공한다.")
    @Test
    void shouldSuccessRefreshAccessToken() {
      // given
      Long memberId = 1234L;
      String subject = String.valueOf(memberId);
      String accessToken = jwtService.createAccessToken(subject, 1L);
      String refreshToken = jwtService.createRefreshToken(subject, System.currentTimeMillis());
      RefreshTokenDto.Request request = RefreshTokenDto.Request.builder()
          .memberId(memberId)
          .refreshToken(BEARER_TOKEN_PREFIX + refreshToken)
          .currentTime(System.currentTimeMillis())
          .build();

      // when
      RefreshTokenDto.Response response = jwtService.refreshAccessToken(request);
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(Decoders.BASE64.decode(SECRET_KEY))
          .build().parseClaimsJws(response.refreshToken()).getBody();

      // then
      assertNotEquals(accessToken, response.accessToken());
      assertEquals(String.valueOf(memberId), claims.getSubject());
    }

    @DisplayName("다른 사용자가 요청하면 실패한다.")
    @Test
    void shouldFailWithInvalidRefreshAccessToken() {
      // given
      Long memberId = 1234L;
      Long anotherMemberId = 54321L;
      String refreshToken = jwtService.createRefreshToken(
          String.valueOf(memberId), System.currentTimeMillis());
      RefreshTokenDto.Request request = RefreshTokenDto.Request.builder()
          .memberId(anotherMemberId)
          .refreshToken(BEARER_TOKEN_PREFIX + refreshToken)
          .currentTime(System.currentTimeMillis())
          .build();

      // when
      Executable callRefreshAccessToken = () -> jwtService.refreshAccessToken(request);

      // then
      assertThrows(JwtDeniedException.class, callRefreshAccessToken);
    }

    @DisplayName("refreshToken이 만료되면 실패한다.")
    @Test
    void shouldFailWithExpiredRefreshAccessToken() {
      // given
      Long memberId = 1234L;
      String refreshToken = jwtService.createRefreshToken(
          String.valueOf(memberId), 1L);
      RefreshTokenDto.Request request = RefreshTokenDto.Request.builder()
          .memberId(memberId)
          .refreshToken(BEARER_TOKEN_PREFIX + refreshToken)
          .currentTime(System.currentTimeMillis())
          .build();

      // when
      Executable callRefreshAccessToken = () -> jwtService.refreshAccessToken(request);

      // then
      assertThrows(JwtExpiredException.class, callRefreshAccessToken);
    }
  }
}