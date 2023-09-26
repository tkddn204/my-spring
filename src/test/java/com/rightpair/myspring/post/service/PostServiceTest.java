package com.rightpair.myspring.post.service;

import com.rightpair.myspring.member.exception.MemberNotFoundException;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.post.dto.CreatePostDto;
import com.rightpair.myspring.post.dto.GetPostDto;
import com.rightpair.myspring.post.entity.Post;
import com.rightpair.myspring.post.exception.PostNotFoundException;
import com.rightpair.myspring.post.repository.PostRepository;
import com.rightpair.myspring.utils.PostTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

public class PostServiceTest extends TestSettings {

  @InjectMocks
  private PostService postService;

  @Mock
  private PostRepository postRepository;

  @Mock
  private MemberRepository memberRepository;

  @DisplayName("getPost() 를 호출할 때")
  @Nested
  class GetPostTest {

    @DisplayName("올바른 postId로 Post를 가져올 수 있다.")
    @Test
    void shouldSuccessToGetPost() {
      // given
      Long memberId = 1234L;
      Post post = PostTestFactory.createTestPost(memberId);

      given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

      // when
      GetPostDto.Response response = postService.getPostById(memberId);

      // then
      assertEquals(memberId, response.memberId());
      assertEquals(post.getTitle(), response.title());
      assertEquals(post.getContent(), response.content());
    }

    @DisplayName("존재하지 않는 postId로 Post를 가져올 수 없다.")
    @Test
    void shouldFailToGetPost() {
      // given
      Long memberId = 1234L;

      given(postRepository.findById(anyLong())).willReturn(Optional.empty());

      // when
      // then
      assertThrows(PostNotFoundException.class,
          () -> postService.getPostById(memberId));
    }
  }

  @DisplayName("getPostList() 를 호출할 때")
  @Nested
  class GetPostListTest {

    @DisplayName("기본 페이지 설정으로 PostList를 가져올 수 있다.")
    @Test
    void shouldSuccessToGetPostList() {
      // given
      List<Post> postList = PostTestFactory.createTestPostList();
      Pageable pageable = Pageable.unpaged();
      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());
      given(postRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).willReturn(postPage);

      // when
      Page<Post> response = postRepository.findAllByOrderByCreatedAtDesc(pageable);

      // then
      assertEquals(postList.size(), response.getTotalElements());
      assertEquals(postPage, response);
    }

    @DisplayName("페이지를 설정해서 PostList를 가져올 수 있다.")
    @ValueSource(ints = {1, 3, 5, 10})
    @ParameterizedTest
    void shouldSuccessToGetPostList(int size) {
      // given
      List<Post> postList = PostTestFactory.createTestPostList();
      Pageable pageable = Pageable.ofSize(size);
      Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());
      given(postRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).willReturn(postPage);

      // when
      Page<Post> response = postRepository.findAllByOrderByCreatedAtDesc(pageable);

      // then
      assertEquals(postList.size(), response.getTotalElements());
      assertEquals(postPage, response);
    }
  }

  @DisplayName("createPost() 를 호출할 때")
  @Nested
  class CreatePostTest {

    @DisplayName("올바른 요청으로 Post를 생성할 수 있다.")
    @Test
    void shouldSuccessToCreatePost() {
      // given
      Long memberId = 1234L;
      Post post = PostTestFactory.createTestPost(memberId);
      CreatePostDto.Request request = CreatePostDto.Request.builder()
          .memberId(memberId)
          .title(post.getTitle())
          .content(post.getContent())
          .build();

      given(postRepository.save(any(Post.class))).willReturn(post);
      given(memberRepository.existsById(anyLong())).willReturn(true);

      // when
      CreatePostDto.Response response = postService.createPost(request);

      // then
      assertEquals(memberId, response.memberId());
      assertEquals(post.getTitle(), response.title());
      assertEquals(post.getContent(), response.content());
    }

    @DisplayName("가입되지 않은 회원은 Post를 생성할 수 없다.")
    @Test
    void shouldFailToCreatePost() {
      // given
      Long memberId = 1234L;
      Post post = PostTestFactory.createTestPost(memberId);
      CreatePostDto.Request request = CreatePostDto.Request.builder()
          .memberId(memberId)
          .title(post.getTitle())
          .content(post.getContent())
          .build();

      given(postRepository.save(any(Post.class))).willReturn(post);
      given(memberRepository.existsById(anyLong())).willReturn(false);

      // when
      // then
      assertThrows(MemberNotFoundException.class,
          () -> postService.createPost(request));
    }
  }
}
