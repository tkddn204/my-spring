package com.rightpair.myspring.member.service;

import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberServiceTest extends TestSettings {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @DisplayName("joinMember(Request)를 호출할 때")
  @Nested
  class JoinMemberTest {

    @DisplayName("Member를 생성할 수 있다.")
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
  }
}