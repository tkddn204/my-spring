package com.rightpair.myspring.post.service;

import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.exception.MemberNotFoundException;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.post.dto.CreatePostDto;
import com.rightpair.myspring.post.dto.GetPostDto;
import com.rightpair.myspring.post.entity.Post;
import com.rightpair.myspring.post.exception.PostNotFoundException;
import com.rightpair.myspring.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;

  public GetPostDto.Response getPostById(Long postId) {
    return GetPostDto.Response.fromEntity(postRepository.findById(postId)
        .orElseThrow(PostNotFoundException::new));
  }

  public List<GetPostDto.Response> getPostList(Pageable pageable) {
    return postRepository.findAllByOrderByCreatedAtDesc(pageable)
        .stream().map(GetPostDto.Response::fromEntity)
        .toList();
  }

  @Transactional
  public CreatePostDto.Response createPost(CreatePostDto.Request request) {
    validateRequestMemberId(request.memberId());

    Post post = postRepository.save(
        Post.builder()
            .member(Member.builder().id(request.memberId()).build())
            .title(request.title())
            .content(request.content())
            .build()
    );
    return CreatePostDto.Response.fromEntity(post);
  }

  private void validateRequestMemberId(Long memberId) {
    if (!memberRepository.existsById(memberId)) {
      throw new MemberNotFoundException();
    }
  }
}
