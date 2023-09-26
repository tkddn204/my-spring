package com.rightpair.myspring.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  BINDING_ERROR("유효성 검사에 실패했습니다."),
  INVALID_ARGUMENT("올바른 형식이 아닙니다."),

  DENIED_JWT_TOKEN("토큰 유효성 검증에 실패했습니다."),
  EXPIRED_JWT_TOKEN("토큰의 만료 기간을 초과했습니다."),

  MEMBER_NOT_FOUND("멤버를 찾을 수 없습니다."),
  ALREADY_EXISTED_EMAIL("이미 존재하는 이메일입니다."),
  INVALID_PASSWORD("올바른 패스워드가 아닙니다."),

  POST_NOT_FOUND("해당 글을 찾을 수 없습니다."),

  INTERNAL_ERROR("서버 내부에 오류가 발생했습니다.");

  private final String value;
}
