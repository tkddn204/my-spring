package com.rightpair.myspring.integration;

import com.rightpair.myspring.jwt.dto.JwtTokenPair;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.dto.LoginMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.service.MemberService;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberLoginTest extends TestSettings {

  private final static String MEMBER_LOGIN_URI = "/api/member/login";

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private MemberService memberService;

  @DisplayName("로그인 요청을 시도할 떄")
  @Nested
  class LoginProcessTest {
    @DisplayName("로그인 프로세스를 성공한다")
    @Test
    void shouldSuccessLoginMemberIntegration() {
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

      // when
      ResponseEntity<LoginMemberDto.Response> response =
          testRestTemplate.postForEntity(MEMBER_LOGIN_URI, loginRequest, LoginMemberDto.Response.class);
      LoginMemberDto.Response actualResponse = Objects.requireNonNull(response.getBody());
      JwtTokenPair jwtTokenPair = actualResponse.jwtTokenPair();
      member.setId(actualResponse.id());
      LoginMemberDto.Response expectedResponse = LoginMemberDto.Response.fromEntityAndPair(member, jwtTokenPair);

      // then
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(expectedResponse, response.getBody());
    }

    @DisplayName("가입하지 않은 이메일을 요청해서 로그인 프로세스 실패")
    @Test
    void shouldFailLoginMemberIntegrationWithInvalidEmail() {
      // given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      Member anotherMember = MemberTestFactory.createUnEncodedTestMember();
      JoinMemberDto.Request joinRequest = JoinMemberDto.Request.builder()
          .email(member.getEmail())
          .password(member.getPassword())
          .nickname(member.getNickname())
          .build();
      memberService.joinMember(joinRequest);
      LoginMemberDto.Request loginRequest = LoginMemberDto.Request.builder()
          .email(anotherMember.getEmail())
          .password(member.getPassword())
          .build();

      // when
      ResponseEntity<LoginMemberDto.Response> response =
          testRestTemplate.postForEntity(MEMBER_LOGIN_URI, loginRequest, LoginMemberDto.Response.class);

      // then
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @DisplayName("올바르지 않은 비밀번호로 요청해서 로그인 프로세스 실패")
    @Test
    void shouldFailLoginMemberIntegrationWithInvalidPassword() {
      // given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      Member anotherMember = MemberTestFactory.createUnEncodedTestMember();
      JoinMemberDto.Request joinRequest = JoinMemberDto.Request.builder()
          .email(member.getEmail())
          .password(member.getPassword())
          .nickname(member.getNickname())
          .build();
      memberService.joinMember(joinRequest);
      LoginMemberDto.Request loginRequest = LoginMemberDto.Request.builder()
          .email(member.getEmail())
          .password(anotherMember.getPassword())
          .build();

      // when
      ResponseEntity<LoginMemberDto.Response> response =
          testRestTemplate.postForEntity(MEMBER_LOGIN_URI, loginRequest, LoginMemberDto.Response.class);

      // then
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
  }
}
