package com.rightpair.myspring.member.exception;

import com.rightpair.myspring.common.error.ErrorCode;
import com.rightpair.myspring.common.error.exception.BusinessException;

public class MemberNotFoundException extends BusinessException {

  private final static ErrorCode errorCode = ErrorCode.MEMBER_NOT_FOUND;

  public MemberNotFoundException() {
    super(errorCode);
  }
}
