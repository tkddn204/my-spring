package com.rightpair.myspring.jwt.dto;

import lombok.Builder;

public class RefreshTokenDto {

  @Builder
  public record RefreshRequest(
      Long memberId,
      String refreshToken
  ) {
  }

  @Builder
  public record Request(
      Long memberId,
      String refreshToken,
      long currentTime
  ) {

    public static Request from(RefreshRequest refreshRequest) {
      return Request.builder()
          .memberId(refreshRequest.memberId())
          .refreshToken(refreshRequest.refreshToken())
          .currentTime(System.currentTimeMillis())
          .build();
    }
  }

  @Builder
  public record Response(
      String grantType,
      String accessToken,
      String refreshToken
  ) {
  }
}
