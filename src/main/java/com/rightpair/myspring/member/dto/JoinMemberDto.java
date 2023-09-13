package com.rightpair.myspring.member.dto;

import com.rightpair.myspring.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class JoinMemberDto {

  @Builder
  public record Request(
      @Email
      @NotBlank
      String email,

      @Size(min = 8, max = 32)
      @NotBlank
      String password,

      @Size(max = 20)
      String nickname
  ) {
  }


  @Builder
  public record Response(
      Long id,
      String email,
      String nickname
  ) {
    public static Response fromEntity(Member member) {
      return Response.builder()
          .id(member.getId())
          .email(member.getEmail())
          .nickname(member.getNickname())
          .build();
    }
  }
}
