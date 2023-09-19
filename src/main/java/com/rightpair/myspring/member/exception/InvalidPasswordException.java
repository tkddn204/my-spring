package com.rightpair.myspring.member.exception;

import com.rightpair.myspring.common.error.ErrorCode;
import com.rightpair.myspring.common.error.exception.BusinessException;

public class InvalidPasswordException extends BusinessException {

  private final static ErrorCode errorCode = ErrorCode.INVALID_PASSWORD;

  public InvalidPasswordException() {
    super(errorCode);
  }
}
