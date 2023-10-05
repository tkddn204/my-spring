package com.rightpair.myspring.post.service;

import com.rightpair.myspring.member.exception.MemberNotFoundException;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.post.dto.CreatePostDto;
import com.rightpair.myspring.post.dto.DeletePostDto;
import com.rightpair.myspring.post.dto.GetPostDto;
import com.rightpair.myspring.post.dto.UpdatePostDto;
import com.rightpair.myspring.post.entity.Post;
import com.rightpair.myspring.post.exception.PostNotFoundException;
import com.rightpair.myspring.post.exception.PostPermissionDeniedException;
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

  @DisplayName("updatePost() 를 호출할 때")
  @Nested
  class UpdatePostTest {

    @DisplayName("올바른 요청으로 Post를 수정할 수 있다.")
    @Test
    void shouldSuccessToUpdatePost() {
      // given
      Long memberId = 1234L;
      Post post = PostTestFactory.createTestPostWithPostId(memberId);
      Post updatedPost = PostTestFactory.createTestPost(memberId);
      UpdatePostDto.Request request = UpdatePostDto.Request.builder()
          .postId(post.getId())
          .memberId(memberId)
          .title(updatedPost.getTitle())
          .content(updatedPost.getContent())
          .build();

      given(memberRepository.existsById(anyLong())).willReturn(true);
      given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

      // when
      UpdatePostDto.Response response = postService.updatePost(request);

      // then
      assertEquals(memberId, response.memberId());
      assertEquals(updatedPost.getTitle(), response.title());
      assertEquals(updatedPost.getContent(), response.content());
    }

    @DisplayName("글의 작성자 ID와 요청의 회원 ID가 다르면 Post를 수정할 수 없다.")
    @Test
    void shouldFailToUpdatePost() {
      // given
      Long memberId = 1234L;
      Long wrongMemberId = 9999L;

      Post post = PostTestFactory.createTestPostWithPostId(memberId);
      Post updatedPost = PostTestFactory.createTestPost(memberId);
      UpdatePostDto.Request request = UpdatePostDto.Request.builder()
          .postId(post.getId())
          .memberId(wrongMemberId)
          .title(updatedPost.getTitle())
          .content(updatedPost.getContent())
          .build();

      given(memberRepository.existsById(wrongMemberId)).willReturn(true);
      given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

      // when
      // then
      assertThrows(PostPermissionDeniedException.class,
          () -> postService.updatePost(request));
    }
  }

  @DisplayName("deletePost() 를 호출할 때")
  @Nested
  class DeletePostTest {

    @DisplayName("올바른 요청으로 Post를 삭제할 수 있다.")
    @Test
    void shouldSuccessToDeletePost() {
      // given
      Long memberId = 1234L;
      Post post = PostTestFactory.createTestPostWithPostId(memberId);
      DeletePostDto.Request request = DeletePostDto.Request.builder()
          .postId(post.getId())
          .memberId(memberId)
          .build();

      given(memberRepository.existsById(anyLong())).willReturn(true);
      given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

      // when
      DeletePostDto.Response response = postService.deletePost(request);

      // then
      assertEquals(post.getId(), response.postId());
    }

    @DisplayName("글의 작성자 ID와 요청의 회원 ID가 다르면 Post를 삭제할 수 없다.")
    @Test
    void shouldFailToDeletePost() {
      // given
      Long memberId = 1234L;
      Long wrongMemberId = 9999L;

      Post post = PostTestFactory.createTestPostWithPostId(memberId);
      DeletePostDto.Request request = DeletePostDto.Request.builder()
          .postId(post.getId())
          .memberId(wrongMemberId)
          .build();

      given(memberRepository.existsById(wrongMemberId)).willReturn(true);
      given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

      // when
      // then
      assertThrows(PostPermissionDeniedException.class,
          () -> postService.deletePost(request));
    }
  }
}
