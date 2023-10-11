package com.rightpair.myspring.docs.jwt;

import com.rightpair.myspring.docs.RestDocsSettings;
import com.rightpair.myspring.jwt.dto.JwtTokenPair;
import com.rightpair.myspring.jwt.dto.RefreshTokenDto;
import com.rightpair.myspring.jwt.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JwtApiDocsTest extends RestDocsSettings {
  @Autowired
  private JwtService jwtService;

  @DisplayName("accessToken 재생성")
  @Nested
  class RefreshAccessTokenAPITest {
    private final static String BEARER_TOKEN_PREFIX = "bearer ";

    @DisplayName("accessToken을 재생성할 수 있다.")
    @Test
    void shouldSuccessToRefreshAccessToken() throws Exception {
      // given
      Long memberId = 1234L;
      JwtTokenPair jwtTokenPair = jwtService.createJwtTokenPair(memberId);
      RefreshTokenDto.RefreshRequest request = RefreshTokenDto.RefreshRequest.builder()
          .grantType("Bearer")
          .refreshToken(jwtTokenPair.refreshToken())
          .build();

      // when
      mockMvc.perform(post("/api/auth/refresh")
              .header("Authorization", BEARER_TOKEN_PREFIX + jwtTokenPair.accessToken())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(request)))
          // then
          .andExpect(status().isCreated())
          .andDo(document("jwt-refreshAccessToken",
              preprocessRequest(prettyPrint()),
              preprocessResponse(prettyPrint()),
              requestFields(
                  fieldWithPath("grantType").type(JsonFieldType.STRING)
                      .description("토큰 권한 타입")
                      .attributes(key("constraints").value("Bearer")),
                  fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                      .description("로그인 시 발급 받은 리프레시 토큰")
              ),
              responseFields(
                  fieldWithPath("grantType").type(JsonFieldType.STRING)
                      .description("토큰 권한 타입")
                      .attributes(key("constraints").value("Bearer")),
                  fieldWithPath("accessToken").type(JsonFieldType.STRING)
                      .description("재생성된 액세스 토큰"),
                  fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                      .description("리프레시 토큰")
              )
          ));
    }
  }
}
