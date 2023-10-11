package com.rightpair.myspring.docs.post;

import com.rightpair.myspring.docs.RestDocsSettings;
import com.rightpair.myspring.jwt.dto.JwtTokenPair;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.dto.LoginMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.member.service.MemberService;
import com.rightpair.myspring.post.dto.CreatePostDto;
import com.rightpair.myspring.post.dto.UpdatePostDto;
import com.rightpair.myspring.post.entity.Post;
import com.rightpair.myspring.post.repository.PostRepository;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.PostTestFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostApiDocsTest extends RestDocsSettings {
  private final static String BEARER_TOKEN_PREFIX = "bearer ";

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PostRepository postRepository;

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("포스트를 조회할 때")
  class GetPostTest {

    private List<Post> postList = PostTestFactory.createTestPostList();

    @BeforeAll
    public void beforeAll() {
      // 포스트 생성
      Member savedTestMember = memberRepository.save(MemberTestFactory.createTestMember());
      postList = postRepository.saveAll(postList.stream().peek(post -> post.setMember(savedTestMember)).toList());
    }

    @Test
    @DisplayName("포스트 리스트의 조회에 성공한다")
    public void shouldSuccessToGetPostList() throws Exception {
      // Given
      // When
      mockMvc.perform(get("/api/post")
              .queryParam("page", "1")
              .queryParam("size", "3")
          )
          // Then
          .andExpect(status().isOk())
          .andDo(document("post-get-list",
              preprocessResponse(prettyPrint()),
              queryParameters(
                  parameterWithName("page").description("페이지 번호"),
                  parameterWithName("size").description("페이지 당 게시글 개수")
              ),
              responseFields(
                  fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                  fieldWithPath("[].memberId").type(JsonFieldType.NUMBER).description("멤버(작성자) ID"),
                  fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                  fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글 내용")
              )
          ));
    }

    @Test
    @DisplayName("포스트 1개의 조회에 성공한다")
    public void shouldSuccessToGetPost() throws Exception {
      // Given
      long postId = postList.get(0).getId();

      // When
      mockMvc.perform(get("/api/post/{post_id}", postId)
              .contentType(MediaType.APPLICATION_JSON)
          )
          // Then
          .andExpect(status().isOk())
          .andDo(document("post-get",
              preprocessResponse(prettyPrint()),
              pathParameters(
                  parameterWithName("post_id").description("게시글 ID")
              ),
              responseFields(
                  fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                  fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버(작성자) ID"),
                  fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                  fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
              )
          ));
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("멤버가 포스트를 작성할 때")
  class CreatePostTest {

    private Member testMember;
    private JwtTokenPair testJwtTokenPair;

    @BeforeAll
    public void beforeAll() {

      // 테스트 멤버 생성
      testMember = MemberTestFactory.createUnEncodedTestMember();
      JoinMemberDto.Request joinRequest = JoinMemberDto.Request.builder()
          .email(testMember.getEmail())
          .password(testMember.getPassword())
          .nickname(testMember.getNickname())
          .build();
      JoinMemberDto.Response joinResponse = memberService.joinMember(joinRequest);
      testMember.setId(joinResponse.id());

      // 테스트 멤버로 로그인
      LoginMemberDto.Request loginRequest = LoginMemberDto.Request.builder()
          .email(testMember.getEmail())
          .password(testMember.getPassword())
          .build();
      LoginMemberDto.Response response = memberService.loginMember(loginRequest);
      testJwtTokenPair = response.jwtTokenPair();
    }

    @Test
    @DisplayName("양식에 맞게 요청을 제출하면 포스트를 작성할 수 있다")
    public void shouldSuccessToCreatePostTestWithValidData() throws Exception {
      // Given
      // 포스트 요청 생성
      Post post = PostTestFactory.createTestPost(testMember.getId());
      CreatePostDto.ControllerRequest controllerRequest = CreatePostDto.ControllerRequest.builder()
          .title(post.getTitle())
          .content(post.getContent())
          .build();

      // When
      mockMvc.perform(post("/api/post")
              .header("Authorization", BEARER_TOKEN_PREFIX + testJwtTokenPair.accessToken())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(controllerRequest))
          )
          // Then
          .andExpect(status().isCreated())
          .andDo(document("post-create",
                  preprocessRequest(prettyPrint()),
                  preprocessResponse(prettyPrint()),
                  requestFields(
                      fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                      fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                          .attributes(key("constraints").value("Non_Blank, 10자 이상"))
                  ),
                  responseFields(
                      fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                      fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                      fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                      fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                  )
              )
          );
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("멤버가 포스트를 수정할 때")
  class UpdatePostTest {

    private List<Post> testPostList = PostTestFactory.createTestPostList(3);
    private Member testMember;
    private JwtTokenPair testJwtTokenPair;

    @BeforeAll
    public void beforeAll() {

      // 테스트 멤버 생성
      testMember = MemberTestFactory.createUnEncodedTestMember();
      JoinMemberDto.Request joinRequest = JoinMemberDto.Request.builder()
          .email(testMember.getEmail())
          .password(testMember.getPassword())
          .nickname(testMember.getNickname())
          .build();
      JoinMemberDto.Response joinResponse = memberService.joinMember(joinRequest);
      testMember.setId(joinResponse.id());

      // 테스트 멤버로 로그인
      LoginMemberDto.Request loginRequest = LoginMemberDto.Request.builder()
          .email(testMember.getEmail())
          .password(testMember.getPassword())
          .build();
      LoginMemberDto.Response response = memberService.loginMember(loginRequest);
      testJwtTokenPair = response.jwtTokenPair();

      // 테스트용 postList 저장
      testPostList = postRepository.saveAll(testPostList.stream().peek(post -> post.setMember(testMember)).toList());
    }

    @Test
    @DisplayName("양식에 맞게 요청을 제출하면 포스트 수정에 성공한다")
    public void shouldSuccessToUpdatePostTestWithValidData() throws Exception {
      // Given
      long originPostId = testPostList.get(0).getId();
      // 포스트 수정 요청 생성
      Post updatedPost = PostTestFactory.createTestPost(testMember.getId());
      UpdatePostDto.ControllerRequest controllerRequest = UpdatePostDto.ControllerRequest.builder()
          .title(updatedPost.getTitle())
          .content(updatedPost.getContent())
          .build();

      // When
      mockMvc.perform(put("/api/post/{post_id}", originPostId)
              .header("Authorization", "bearer " + testJwtTokenPair.accessToken())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(controllerRequest))
          )
          // Then
          .andExpect(status().isOk())
          .andDo(document("post-update",
                  preprocessRequest(prettyPrint()),
                  preprocessResponse(prettyPrint()),
                  pathParameters(
                      parameterWithName("post_id").description("게시글 ID")
                  ),
                  requestFields(
                      fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                      fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                          .attributes(key("constraints").value("Non_Blank, 10자 이상"))
                  ),
                  responseFields(
                      fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                      fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                      fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                      fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                  )
              )
          );
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("유저가 포스트를 삭제할 때")
  class DeletePostTest {

    private List<Post> testPostList = PostTestFactory.createTestPostList(1);
    private Member testMember;
    private JwtTokenPair testJwtTokenPair;

    @BeforeAll
    public void beforeAll() {

      // 테스트 멤버 생성
      testMember = MemberTestFactory.createUnEncodedTestMember();
      JoinMemberDto.Request joinRequest = JoinMemberDto.Request.builder()
          .email(testMember.getEmail())
          .password(testMember.getPassword())
          .nickname(testMember.getNickname())
          .build();
      JoinMemberDto.Response joinResponse = memberService.joinMember(joinRequest);
      testMember.setId(joinResponse.id());

      // 테스트 멤버로 로그인
      LoginMemberDto.Request loginRequest = LoginMemberDto.Request.builder()
          .email(testMember.getEmail())
          .password(testMember.getPassword())
          .build();
      LoginMemberDto.Response response = memberService.loginMember(loginRequest);
      testJwtTokenPair = response.jwtTokenPair();

      // 테스트용 postList 저장
      testPostList = postRepository.saveAll(testPostList.stream().peek(post -> post.setMember(testMember)).toList());
    }

    @Test
    @DisplayName("요청을 보내면 포스트 삭제에 성공한다")
    public void shouldSuccessToDeletePostTestWithValidData() throws Exception {
      // Given
      long originPostId = testPostList.get(0).getId();

      // When
      mockMvc.perform(delete("/api/post/{post_id}", originPostId)
              .header("Authorization", "bearer " + testJwtTokenPair.accessToken())
          )
          // Then
          .andExpect(status().isOk())
          .andDo(document("post-delete",
                  preprocessResponse(prettyPrint()),
                  pathParameters(
                      parameterWithName("post_id").description("게시글 ID")
                  ),
                  responseFields(
                      fieldWithPath("postId").type(JsonFieldType.NUMBER).description("삭제된 게시글 ID")
                  )
              )
          );
    }
  }
}
