package com.rightpair.myspring.post.dto;

import lombok.Builder;

public class DeletePostDto {

  @Builder
  public record Request
      (
          Long memberId,
          Long postId
      ) {
    public static Request from(Long memberId, Long postId) {
      return Request.builder()
          .memberId(memberId)
          .postId(postId)
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
