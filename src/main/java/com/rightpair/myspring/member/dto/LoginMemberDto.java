package com.rightpair.myspring.member.dto;

import com.rightpair.myspring.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class LoginMemberDto {

  @Builder
  public record Request(
      @Email(message = "올바른 이메일 형식이 아닙니다.")
      @NotBlank(message = "이메일을 입력해 주십시오.")
      String email,

      @Size(min = 8, max = 32, message = "비밀번호는 8자 이상 32자 이하여야 합니다.")
      @NotBlank(message = "비밀번호를 입력해 주십시오.")
      String password
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
