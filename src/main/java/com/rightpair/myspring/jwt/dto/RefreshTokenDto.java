package com.rightpair.myspring.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

public class RefreshTokenDto {

  @AllArgsConstructor
  @Builder
  public record Request(
      Long memberId,
      String refreshToken,
      long currentTime
  ) {
  }


  @AllArgsConstructor
  @Builder
  public record Response(
      String accessToken,
      String refreshToken
  ) {
  }
}
