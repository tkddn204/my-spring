package com.rightpair.myspring.integration;

import com.rightpair.myspring.jwt.dto.JwtTokenPair;
import com.rightpair.myspring.jwt.dto.RefreshTokenDto;
import com.rightpair.myspring.jwt.service.JwtService;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.dto.LoginMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.member.service.MemberService;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MemberJwtTest extends TestSettings {

  private final static String JWT_REFRESH_URI = "/api/auth/refresh";

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private JwtService jwtService;

  @DisplayName("로그인 성공 후 엑세스 토큰 리프레시에 성공한다")
  @Test
  void shouldSuccessLoginMemberIntegration() throws InterruptedException {
    // given

    // 회원가입
    Member member = MemberTestFactory.createUnEncodedTestMember();
    JoinMemberDto.Request joinRequest = JoinMemberDto.Request.builder()
        .email(member.getEmail())
        .password(member.getPassword())
        .nickname(member.getNickname())
        .build();
    memberService.joinMember(joinRequest);

    // 로그인
    LoginMemberDto.Request loginRequest = LoginMemberDto.Request.builder()
        .email(member.getEmail())
        .password(member.getPassword())
        .build();
    LoginMemberDto.Response loginResponse = memberService.loginMember(loginRequest);
    JwtTokenPair firstJwtTokenPair = loginResponse.jwtTokenPair();

    // 시간차를 두어 다른 시간의 리프레시 토큰을 생성하기 위해 딜레이를 줌
    Thread.sleep(new Random().nextInt(1000) + 500);

    // 토큰 리프레시 요청
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "bearer " + firstJwtTokenPair.accessToken());
    RefreshTokenDto.RefreshRequest refreshAccessTokenRequest = RefreshTokenDto.RefreshRequest.builder()
        .memberId(loginResponse.id())
        .grantType("Bearer")
        .refreshToken(firstJwtTokenPair.refreshToken())
        .build();

    // when
    ResponseEntity<RefreshTokenDto.Response> response =
        testRestTemplate.exchange(JWT_REFRESH_URI,
            HttpMethod.POST,
            new HttpEntity<>(refreshAccessTokenRequest, headers),
            RefreshTokenDto.Response.class);

    RefreshTokenDto.Response actualResponse = Objects.requireNonNull(response.getBody());

    // then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotEquals(firstJwtTokenPair.accessToken(), actualResponse.accessToken());
    assertEquals(firstJwtTokenPair.refreshToken(), actualResponse.refreshToken());
  }

  @DisplayName("로그인 성공 후 잘못된 액세스 토큰으로 요청하면 엑세스 토큰 리프레시에 실패한다")
  @Test
  void shouldFailLoginMemberIntegrationWithInvalidAccessToken() {
    // given
    Member member = MemberTestFactory.createUnEncodedTestMember();

    JoinMemberDto.Request joinRequest = JoinMemberDto.Request.builder()
        .email(member.getEmail())
        .password(member.getPassword())
        .nickname(member.getNickname())
        .build();
    memberService.joinMember(joinRequest);

    LoginMemberDto.Request loginRequest = LoginMemberDto.Request.builder()
        .email(member.getEmail())
        .password(member.getPassword())
        .build();
    LoginMemberDto.Response loginResponse = memberService.loginMember(loginRequest);
    JwtTokenPair firstJwtTokenPair = loginResponse.jwtTokenPair();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    String invalidAccessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjk1MjI4MDQ1LCJleHAiOjE2OTUyMjg5NDV9.U05ZE12MkEzi0EKuEqpQnwFMYl-5Yhp0x1U3muQm76TQxSgsjzzToInd3Cw8RxbDGNiV2QYFG-8hLhMDi6hq1w";
    headers.add("Authorization", "bearer " + invalidAccessToken);
    RefreshTokenDto.RefreshRequest refreshAccessTokenRequest = RefreshTokenDto.RefreshRequest.builder()
        .memberId(loginResponse.id())
        .grantType("bearer")
        .refreshToken(firstJwtTokenPair.refreshToken())
        .build();

    // when
    ResponseEntity<RefreshTokenDto.Response> response =
        testRestTemplate.exchange(JWT_REFRESH_URI,
            HttpMethod.POST,
            new HttpEntity<>(refreshAccessTokenRequest, headers),
            RefreshTokenDto.Response.class);

    // then
    assertFalse(response.hasBody());
//    assertEquals(e.getMessage(), "토큰 유효성 검증에 실패했습니다.");
  }
}
