package com.rightpair.myspring.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  MEMBER_NOT_FOUND("멤버를 찾을 수 없습니다."),
  ALREADY_EXISTED_EMAIL("이미 존재하는 이메일입니다."),
  INVALID_PASSWORD("올바른 패스워드가 아닙니다."),

  INTERNAL_ERROR("서버 내부에 오류가 발생했습니다.");

  private final String value;
}
