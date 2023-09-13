package com.rightpair.myspring.member.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rightpair.myspring.member.dto.GetMemberDto;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class MemberApiTest extends TestSettings {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MemberRepository memberRepository;

  @Nested
  @DisplayName("유저가 회원가입을 할 때")
  class JoinMemberTest {
    @Test
    @DisplayName("양식에 맞게 요청을 제출하면 회원가입을 할 수 있다")
    public void joinMemberTestWithValidData() throws Exception {
      // Given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      JoinMemberDto.Request request = JoinMemberDto.Request.builder()
          .email(member.getEmail())
          .nickname(member.getNickname())
          .password(member.getPassword())
          .build();
      JoinMemberDto.Response expectedResponse = JoinMemberDto.Response.fromEntity(member);
      String expected = objectMapper.writeValueAsString(expectedResponse);

      // When
      String jsonResponse = mockMvc.perform(post("/api/member/join")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(request))
          )
          .andExpect(status().isCreated())
          .andReturn()
          .getResponse()
          .getContentAsString();
      JsonNode jsonActual = objectMapper.readTree(jsonResponse);
      ((ObjectNode) jsonActual).putNull("id");

      // Then
      assertThat(jsonActual.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("잘못된 이메일을 제출하면 회원가입을 할 수 없다")
    public void joinMemberTestWithInValidData() throws Exception {
      // Given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      JoinMemberDto.Request request = JoinMemberDto.Request.builder()
          .email("this-is-not-email")
          .nickname(member.getNickname())
          .password(member.getPassword())
          .build();

      // When
      mockMvc.perform(post("/api/member/join")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(request))
          )
          // Then
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("유저를 ID로 검색할 때")
  class MemberTest {
    @Test
    @DisplayName("존재하는 ID로 검색하면 유저를 반환해야 한다")
    public void shouldGetMemberTestWithValidData() throws Exception {
      // Given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      Member savedMember = memberRepository.save(member);
      GetMemberDto.Response response = GetMemberDto.Response.fromEntity(savedMember);

      // When
      mockMvc.perform(get("/api/member/" + savedMember.getId())
              .contentType(MediaType.APPLICATION_JSON)
          )
          // Then
          .andExpect(status().isOk())
          .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 검색하면 유저를 반환할 수 없다")
    public void shouldGetMemberTestWithInValidData() throws Exception {
      // Given
      long id = 12345678L;

      // When
      mockMvc.perform(get("/api/member/" + id)
              .contentType(MediaType.APPLICATION_JSON)
          )
          // Then
          .andExpect(status().isInternalServerError());
    }
  }
}