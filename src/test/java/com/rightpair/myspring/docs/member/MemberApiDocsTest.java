package com.rightpair.myspring.docs.member;

import com.rightpair.myspring.docs.RestDocsSettings;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.dto.LoginMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.utils.MemberTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberApiDocsTest extends RestDocsSettings {

  @Autowired
  private MemberRepository memberRepository;

  @DisplayName("멤버 회원가입")
  @Nested
  class JoinMemberApiTest {

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
                  fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일")
                      .attributes(key("constraints").value("Non_Blank, EMAIL FORMAT")),
                  fieldWithPath("password").type(JsonFieldType.STRING).description("멤버 비밀번호")
                      .attributes(key("constraints").value("Non_Blank, 8자 이상 32자 이하")),
                  fieldWithPath("nickname").type(JsonFieldType.STRING).description("멤버 닉네임")
                      .attributes(key("constraints").value("Non_Blank, 최대 20자까지"))
              ),
              responseFields(
                  fieldWithPath("id").type(JsonFieldType.NUMBER).description("멤버 ID"),
                  fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일"),
                  fieldWithPath("nickname").type(JsonFieldType.STRING).description("멤버 닉네임")
              )
          ));
    }
  }

  @Nested
  @DisplayName("멤버 로그인")
  class LoginMemberApiTest {

    @Test
    @DisplayName("멤버는 로그인을 할 수 있다")
    public void shouldSuccessToLogin() throws Exception {
      // Given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      Member savedMember = MemberTestFactory.createTestMemberFromPassword(member.getPassword());
      savedMember.setEmail(member.getEmail());
      savedMember.setNickname(member.getNickname());
      memberRepository.save(savedMember);
      LoginMemberDto.Request request = LoginMemberDto.Request.builder()
          .email(member.getEmail())
          .password(member.getPassword())
          .build();

      // When
      mockMvc.perform(post("/api/member/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(request))
          )
          // Then
          .andExpect(status().isOk())
          .andDo(document("member-login",
              preprocessRequest(prettyPrint()),
              preprocessResponse(prettyPrint()),
              requestFields(
                  fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일")
                      .attributes(key("constraints").value("Non_Blank, EMAIL FORMAT")),
                  fieldWithPath("password").type(JsonFieldType.STRING).description("멤버 비밀번호")
                      .attributes(key("constraints").value("Non_Blank, 8자 이상 32자 이하"))
              ),
              responseFields(
                  fieldWithPath("id").type(JsonFieldType.NUMBER).description("멤버 ID"),
                  fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일"),
                  fieldWithPath("nickname").type(JsonFieldType.STRING).description("멤버 닉네임"),
                  fieldWithPath("jwtTokenPair['accessToken']").type(JsonFieldType.STRING).description("JWT 액세스 토큰"),
                  fieldWithPath("jwtTokenPair['refreshToken']").type(JsonFieldType.STRING).description("JWT 리프레시 토큰")
              )
          ));
    }
  }

  @Nested
  @DisplayName("멤버 조회")
  class GetMemberTest {
    @Test
    @DisplayName("사용자는 존재하는 멤버 ID로 검색하면 유저를 조회할 수 있다.")
    public void shouldSuccessToGetMember() throws Exception {
      // Given
      Member member = MemberTestFactory.createUnEncodedTestMember();
      Member savedMember = memberRepository.save(member);

      // When
      mockMvc.perform(get("/api/member/{id}", savedMember.getId())
              .contentType(MediaType.APPLICATION_JSON)
          )
          // Then
          .andExpect(status().isOk())
          .andDo(document("member-get",
              preprocessResponse(prettyPrint()),
              pathParameters(
                  parameterWithName("id").description("멤버 ID")
              ),
              responseFields(
                  fieldWithPath("id").type(JsonFieldType.NUMBER).description("멤버 ID"),
                  fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일"),
                  fieldWithPath("nickname").type(JsonFieldType.STRING).description("멤버 닉네임")
              )
          ));
    }
  }
}
