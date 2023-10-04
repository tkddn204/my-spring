package com.rightpair.myspring.post.dto;

import com.rightpair.myspring.post.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class UpdatePostDto {

  @Builder
  public record ControllerRequest
      (
          Long postId,
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
          Long postId,
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
  }

  @Builder
  public record Response
      (
          Long memberId,
          Long postId,
          String title,
          String content
      ) {

    public static Response fromEntity(Post post) {
      return Response.builder()
          .postId(post.getId())
          .memberId(post.getMember().getId())
          .title(post.getTitle())
          .content(post.getContent())
          .build();
    }
  }
}
