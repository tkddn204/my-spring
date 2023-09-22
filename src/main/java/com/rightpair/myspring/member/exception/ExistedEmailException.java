package com.rightpair.myspring.member.exception;

import com.rightpair.myspring.common.error.ErrorCode;
import com.rightpair.myspring.common.error.exception.BusinessException;

public class ExistedEmailException extends BusinessException {

  private final static ErrorCode errorCode = ErrorCode.ALREADY_EXISTED_EMAIL;

  public ExistedEmailException() {
    super(errorCode);
  }
}
