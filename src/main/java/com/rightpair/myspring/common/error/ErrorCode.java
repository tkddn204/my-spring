package com.rightpair.myspring.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  BINDING_ERROR("유효성 검사에 실패했습니다."),

  DENIED_JWT_TOKEN("토큰 유효성 검증에 실패했습니다."),
  EXPIRED_JWT_TOKEN("토큰의 만료 기간을 초과했습니다."),

  INTERNAL_ERROR("서버 내부 오류");

  private final String value;
}
