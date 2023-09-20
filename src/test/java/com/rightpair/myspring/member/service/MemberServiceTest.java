package com.rightpair.myspring.member.service;

import com.rightpair.myspring.jwt.dto.JwtTokenPair;
import com.rightpair.myspring.jwt.service.JwtService;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.dto.LoginMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.exception.ExistedEmailException;
import com.rightpair.myspring.member.exception.InvalidPasswordException;
import com.rightpair.myspring.member.exception.MemberNotFoundException;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class MemberServiceTest extends TestSettings {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private JwtService jwtService;

  @Mock
  private MemberRepository memberRepository;

  @DisplayName("joinMember(Request)를 호출할 때")
  @Nested
  class JoinMemberTest {

    @DisplayName("Member 가입을 할 수 있다.")
    @Test
    void shouldSuccessToJoin() {
      // Given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      JoinMemberDto.Request request = JoinMemberDto.Request.builder()
          .email(member.getEmail())
          .password(member.getPassword())
          .nickname(member.getNickname())
          .build();
      given(memberRepository.save(any(Member.class))).willReturn(member);

      // When
      JoinMemberDto.Response response = memberService.joinMember(request);

      // Then
      assertEquals(request.email(), response.email());
      assertEquals(request.nickname(), response.nickname());
    }

    @DisplayName("이미 가입된 회원의 email이 주어질 경우 Member 가입을 할 수 없다.")
    @Test
    void shouldFailToJoinWithExistingEmail() {
      // Given
      Member joinedMember = MemberTestFactory.createTestMember();
      JoinMemberDto.Request request = JoinMemberDto.Request.builder()
          .email(joinedMember.getEmail())
          .password(joinedMember.getPassword())
          .nickname(joinedMember.getNickname())
          .build();

      // When
      when(memberRepository.existsByEmail(joinedMember.getEmail()))
          .thenReturn(true);

      // Then
      assertThrows(ExistedEmailException.class, () -> memberService.joinMember(request));
    }
  }


  @DisplayName("loginMember(Request)를 호출할 때")
  @Nested
  class LoginMemberTest {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @DisplayName("Member 로그인을 할 수 있다.")
    @Test
    void shouldLoginMemberSuccess() {
      // Given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      LoginMemberDto.Request request = LoginMemberDto.Request.builder()
          .email(member.getEmail())
          .password(member.getPassword())
          .build();
      member.setPassword(passwordEncoder.encode(member.getPassword()));
      JwtTokenPair jwtTokenPair = JwtTokenPair.builder()
          .accessToken("fake-access-token")
          .refreshToken("fake-refresh-token")
          .build();
      given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
      given(jwtService.createJwtTokenPair(member.getId())).willReturn(jwtTokenPair);
      // When
      LoginMemberDto.Response response = memberService.loginMember(request);

      // Then
      assertEquals(request.email(), response.email());
    }

    @DisplayName("요청 email에 대응하는 Member 정보가 없으면 로그인을 할 수 없다.")
    @Test
    void shouldLoginMemberFailedWithNotFoundMember() {
      // Given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      LoginMemberDto.Request request = LoginMemberDto.Request.builder()
          .email(member.getEmail())
          .password(member.getPassword())
          .build();

      // When
      Assertions.assertThatThrownBy(() -> memberService.loginMember(request))
          .isInstanceOf(MemberNotFoundException.class)
          // Then
          .hasMessage("멤버를 찾을 수 없습니다.");
    }

    @DisplayName("요청 비밀번호가 틀리면 로그인을 할 수 없다.")
    @Test
    void shouldLoginMemberFailedWithInvalidPassword() {
      // Given
      Member member = MemberTestFactory.createTestMember();
      LoginMemberDto.Request request = LoginMemberDto.Request.builder()
          .email(member.getEmail())
          .password("wrong-password")
          .build();
      given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

      // When
      Assertions.assertThatThrownBy(() -> memberService.loginMember(request))
          .isInstanceOf(InvalidPasswordException.class)
          // Then
          .hasMessage("올바른 패스워드가 아닙니다.");
    }
  }
}