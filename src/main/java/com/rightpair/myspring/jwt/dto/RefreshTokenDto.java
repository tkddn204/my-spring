package com.rightpair.myspring.jwt.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

public class RefreshTokenDto {

  @Builder
  public record RefreshRequest(
      Long memberId,
      @Pattern(regexp = "Bearer")
      String grantType,
      @Pattern(regexp = "^[\\w-]*\\.[\\w-]*\\.[\\w-]*$", message = "올바른 JWT 포맷이 아닙니다.")
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
