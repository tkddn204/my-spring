package com.rightpair.myspring.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  INTERNAL_ERROR("서버 내부 오류");

  private final String value;
}
