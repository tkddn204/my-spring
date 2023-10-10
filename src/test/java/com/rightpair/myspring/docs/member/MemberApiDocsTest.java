package com.rightpair.myspring.docs.member;

import com.rightpair.myspring.docs.RestDocsSettings;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.utils.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberApiDocsTest extends RestDocsSettings {

  @DisplayName("Member Api")
  @Nested
  class JoinMemberTest {

    @DisplayName("사용자는 회원가입을 할 수 있다.")
    @Test
    void shouldSuccessToJoin() throws Exception {
      // Given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      JoinMemberDto.Request request = JoinMemberDto.Request.builder()
          .email(member.getEmail())
          .nickname(member.getNickname())
          .password(member.getPassword())
          .build();

      // When
      mockMvc.perform(post("/api/member/join")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
          )
          // Then
          .andExpect(status().isCreated())
          .andDo(document("member-join",
              preprocessRequest(prettyPrint()),
              preprocessResponse(prettyPrint()),
              requestFields(
                  fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일")
                      .attributes(key("constraints").value("Non_Blank, EMAIL FORMAT")),
                  fieldWithPath("password").type(JsonFieldType.STRING).description("사용자 비밀번호")
                      .attributes(key("constraints").value("Non_Blank, 8자 이상 32자 이하")),
                  fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임")
                      .attributes(key("constraints").value("Non_Blank, 최대 20자까지"))
              ),
              responseFields(
                  fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 아이디"),
                  fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
                  fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임")
              )
          ));
    }
  }
}
