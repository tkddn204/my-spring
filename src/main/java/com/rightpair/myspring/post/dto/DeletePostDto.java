package com.rightpair.myspring.post.dto;

import lombok.Builder;

public class DeletePostDto {
  @Builder
  public record ControllerRequest
      (
          Long postId
      ) {
  }

  @Builder
  public record Request
      (
          Long memberId,
          Long postId
      ) {
    public static Request from(Long memberId, DeletePostDto.ControllerRequest request) {
      return Request.builder()
          .memberId(memberId)
          .postId(request.postId())
          .build();
    }
  }

  @Builder
  public record Response
      (
          Long postId
      ) {
  }
}
