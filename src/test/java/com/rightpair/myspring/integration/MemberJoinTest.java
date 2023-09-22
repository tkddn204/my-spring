package com.rightpair.myspring.integration;

import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberJoinTest extends TestSettings {

  private final static String MEMBER_JOIN_URI = "/api/member/join";

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private MemberRepository memberRepository;

  @DisplayName("회원 가입 프로세스 성공")
  @Test
  void shouldSuccessJoinMemberIntegration() {
    // given
    Member member = MemberTestFactory.createUnEncodedTestMember();
    JoinMemberDto.Request request = JoinMemberDto.Request.builder()
        .email(member.getEmail())
        .password(member.getPassword())
        .nickname(member.getNickname())
        .build();

    // when
    ResponseEntity<JoinMemberDto.Response> response =
        testRestTemplate.postForEntity(MEMBER_JOIN_URI, request, JoinMemberDto.Response.class);

    Member createdMember = memberRepository.findByEmail(request.email()).get();
    JoinMemberDto.Response expectedResponse = JoinMemberDto.Response.fromEntity(createdMember);

    // then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(expectedResponse, response.getBody());
  }

  @DisplayName("이메일 형식이 일치하지 않아서 회원 가입 프로세스 실패")
  @Test
  void shouldFailJoinMemberIntegrationWithInvalidEmail() {
    // given
    Member member = MemberTestFactory.createUnEncodedTestMember();
    JoinMemberDto.Request request = JoinMemberDto.Request.builder()
        .email("wrong-email")
        .password(member.getPassword())
        .nickname(member.getNickname())
        .build();

    // when
    ResponseEntity<JoinMemberDto.Response> response =
        testRestTemplate.postForEntity(MEMBER_JOIN_URI, request, JoinMemberDto.Response.class);
    Optional<Member> wrongMember = memberRepository.findByEmail(member.getEmail());

    // then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(wrongMember.isEmpty());
  }
}
