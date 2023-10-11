package com.rightpair.myspring.jwt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rightpair.myspring.jwt.dto.JwtTokenPair;
import com.rightpair.myspring.jwt.dto.RefreshTokenDto;
import com.rightpair.myspring.jwt.service.JwtService;
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class JwtApiTest extends TestSettings {
  private final static String BEARER_TOKEN_PREFIX = "Bearer ";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtService jwtService;

  @DisplayName("refreshAccessToken API를 호출할 때")
  @Nested
  class RefreshAccessTokenAPITest {

    @DisplayName("accessToken을 재생성할 수 있다.")
    @Test
    void shouldSuccessRefreshAccessToken() throws Exception {
      // given
      Long memberId = 1234L;
      JwtTokenPair jwtTokenPair = jwtService.createJwtTokenPair(memberId);
      RefreshTokenDto.RefreshRequest request = RefreshTokenDto.RefreshRequest.builder()
          .grantType("Bearer")
          .refreshToken(jwtTokenPair.refreshToken())
          .build();

      // when
      mockMvc.perform(post("/api/auth/refresh")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization", BEARER_TOKEN_PREFIX + jwtTokenPair.accessToken())
              .content(objectMapper.writeValueAsBytes(request)))
          // then
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.refreshToken").value(jwtTokenPair.refreshToken()));
    }

    @DisplayName("JWT 형식에 맞지 않는 문자열로 요청하면 실패한다.")
    @Test
    void shouldFailRefreshAccessTokenWithInvalidString() throws Exception {
      // given
      Long memberId = 1234L;
      JwtTokenPair jwtTokenPair = jwtService.createJwtTokenPair(memberId);
      RefreshTokenDto.RefreshRequest request = RefreshTokenDto.RefreshRequest.builder()
          .grantType("Bearer")
          .refreshToken("wrong-refresh-token")
          .build();

      // when
      mockMvc.perform(post("/api/auth/refresh")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization", BEARER_TOKEN_PREFIX + jwtTokenPair.accessToken())
              .content(objectMapper.writeValueAsBytes(request)))
          // then
          .andExpect(status().isBadRequest());
    }
  }
}