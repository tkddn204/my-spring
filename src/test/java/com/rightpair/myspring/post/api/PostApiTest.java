package com.rightpair.myspring.post.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
class PostApiTest extends TestSettings {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PostRepository postRepository;

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("유저가 포스트를 작성할 때")
  class CreatePostTest {

    private final List<Post> failTestPostList = PostTestFactory.createFailTestPostList();
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
      CreatePostDto.Response expectedResponse = CreatePostDto.Response.fromEntity(post);

      // When
      String jsonResponse = mockMvc.perform(post("/api/post")
              .header("Authorization", "bearer " + testJwtTokenPair.accessToken())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(controllerRequest))
          ).andExpect(status().isCreated())
          .andReturn()
          .getResponse()
          .getContentAsString();
      JsonNode actualJson = objectMapper.readTree(jsonResponse);

      // Then
      assertTrue(actualJson.hasNonNull("postId"));
      assertEquals(expectedResponse.title(), actualJson.get("title").asText());
      assertEquals(expectedResponse.content(), actualJson.get("content").asText());
    }

    // 0 : 제목이 비어 있을 경우
    // 1 : 내용이 비어 있을 경우
    // 2 : 내용이 10자 이하일 경우
    @ValueSource(ints = {0, 1, 2})
    @ParameterizedTest
    @DisplayName("양식에 맞지 않게 요청을 제출하면 포스트를 작성할 수 있다")
    public void shouldFailToCreatePostTestWithInValidData(int postIndex) throws Exception {
      // Given
      // 포스트 요청 생성
      Post post = failTestPostList.get(postIndex);
      CreatePostDto.ControllerRequest controllerRequest = CreatePostDto.ControllerRequest.builder()
          .title(post.getTitle())
          .content(post.getContent())
          .build();

      // When
      mockMvc.perform(post("/api/post")
              .header("Authorization", "bearer " + testJwtTokenPair.accessToken())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(controllerRequest))
          )
          // Then
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errorCode").value("BINDING_ERROR"));
    }
  }

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
    @DisplayName("조회에 성공한다")
    public void shouldSuccessToGetPost() throws Exception {
      // Given
      long postId = postList.get(0).getId();
      Post expectedPost = postList.get(0);

      // When
      mockMvc.perform(get("/api/post/" + postId)
              .contentType(MediaType.APPLICATION_JSON)
          )
          // Then
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.title").value(expectedPost.getTitle()))
          .andExpect(jsonPath("$.content").value(expectedPost.getContent()));
    }

    @Test
    @DisplayName("존재하지 않는 게시글 번호를 입력하면 조회에 실패한다")
    public void shouldFailToGetPost() throws Exception {
      // Given
      long postId = 9999L;
      // When
      mockMvc.perform(get("/api/post/" + postId)
              .contentType(MediaType.APPLICATION_JSON)
          )
          // Then
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errorCode").value("POST_NOT_FOUND"));
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("유저가 포스트를 수정할 때")
  class UpdatePostTest {

    private final List<Post> failTestPostList = PostTestFactory.createFailTestPostList();
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
      testPostList.forEach(post -> post.setMember(testMember));
      testPostList = postRepository.saveAll(testPostList);
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
      UpdatePostDto.Response expectedResponse = UpdatePostDto.Response.fromEntity(updatedPost);

      // When
      String jsonResponse = mockMvc.perform(put("/api/post/" + originPostId)
              .header("Authorization", "bearer " + testJwtTokenPair.accessToken())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(controllerRequest))
          ).andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();
      JsonNode actualJson = objectMapper.readTree(jsonResponse);

      // Then
      assertTrue(actualJson.hasNonNull("postId"));
      assertEquals(expectedResponse.title(), actualJson.get("title").asText());
      assertEquals(expectedResponse.content(), actualJson.get("content").asText());
    }

    // 0 : 제목이 비어 있을 경우
    // 1 : 내용이 비어 있을 경우
    // 2 : 내용이 10자 이하일 경우
    @ValueSource(ints = {0, 1, 2})
    @ParameterizedTest
    @DisplayName("양식에 맞지 않게 요청을 제출하면 포스트 수정에 실패한다")
    public void shouldFailToUpdatePostTestWithInValidData(int postIndex) throws Exception {
      // Given
      long originPostId = testPostList.get(0).getId();
      // 포스트 수정 요청 생성
      Post post = failTestPostList.get(postIndex);
      UpdatePostDto.ControllerRequest controllerRequest = UpdatePostDto.ControllerRequest.builder()
          .title(post.getTitle())
          .content(post.getContent())
          .build();

      // When
      mockMvc.perform(put("/api/post/" + originPostId)
              .header("Authorization", "bearer " + testJwtTokenPair.accessToken())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsBytes(controllerRequest))
          )
          // Then
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errorCode").value("BINDING_ERROR"));
    }
  }
}