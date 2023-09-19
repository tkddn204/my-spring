package com.rightpair.myspring.jwt.dto;

import lombok.Builder;

public class RefreshTokenDto {

  @Builder
  public record Request(
      Long memberId,
      String refreshToken,
      long currentTime
  ) {
  }


  @Builder
  public record Response(
      String accessToken,
      String refreshToken
  ) {
  }
}
