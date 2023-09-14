package com.rightpair.myspring.member.service;

import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.dto.LoginMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class MemberServiceTest extends TestSettings {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @DisplayName("joinMember(Request)를 호출할 때")
  @Nested
  class JoinMemberTest {

    @DisplayName("Member 가입을 할 수 있다.")
    @Test
    void shouldJoinMemberSuccess() {
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

      // Than
      assertNotNull(response);
    }

    @DisplayName("이미 가입된 회원의 email일 경우 Member 가입을 할 수 없다.")
    @Test
    void shouldJoinMemberFailedWithNotFoundMember() {
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

      // Than
      assertNotNull(response);
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
      given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

      // When
      LoginMemberDto.Response response = memberService.loginMember(request);

      // Than
      assertNotNull(response);
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
          .isInstanceOf(EntityNotFoundException.class)
          // Than
          .hasMessage("회원이 존재하지 않습니다.");
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
          .isInstanceOf(IllegalArgumentException.class)
          // Than
          .hasMessage("올바른 패스워드가 아닙니다.");
    }
  }
}