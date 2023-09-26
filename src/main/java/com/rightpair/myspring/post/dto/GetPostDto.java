package com.rightpair.myspring.post.dto;

import com.rightpair.myspring.post.entity.Post;
import lombok.Builder;

public class GetPostDto {
  @Builder
  public record Response
      (
          Long postId,
          Long memberId,
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
