package com.rightpair.myspring.post.dto;

import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.post.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class CreatePostDto {

  @Builder
  public record ControllerRequest
      (
          @NotBlank(message = "제목을 입력해주세요.")
          String title,
          @NotBlank(message = "내용을 입력해주세요.")
          @Size(min = 10, message = "최소 10자 이상 입력해주세요.")
          String content
      ) {
  }

  @Builder
  public record Request
      (
          Long memberId,
          String title,
          String content
      ) {

    public static Request from(Long memberId, ControllerRequest controllerRequest) {
      return Request.builder()
          .memberId(memberId)
          .title(controllerRequest.title())
          .content(controllerRequest.content())
          .build();
    }

    public static Post toEntity(Request request) {
      return Post.builder()
          .member(Member.builder()
              .id(request.memberId())
              .build()
          )
          .title(request.title())
          .content(request.content())
          .build();
    }
  }

  @Builder
  public record Response
      (
          Long id,
          Long memberId,
          String title,
          String content
      ) {

    public static Response fromEntity(Post post) {
      return Response.builder()
          .id(post.getId())
          .memberId(post.getMember().getId())
          .title(post.getTitle())
          .content(post.getContent())
          .build();
    }
  }
}
