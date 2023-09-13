package com.rightpair.myspring.member.dto;

import com.rightpair.myspring.member.entity.Member;
import lombok.Builder;

public class GetMemberDto {

  @Builder
  public record Response(
      Long id,
      String email,
      String nickname
  ) {

    public static Response fromEntity(Member member) {
      return builder()
          .id(member.getId())
          .email(member.getEmail())
          .nickname(member.getNickname())
          .build();
    }
  }
}
