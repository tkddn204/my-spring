package com.rightpair.myspring.jwt.exception;

import com.rightpair.myspring.common.error.ErrorCode;
import com.rightpair.myspring.common.error.exception.JwtSecurityException;

public class JwtExpiredException extends JwtSecurityException {

  private final static ErrorCode errorCode = ErrorCode.EXPIRED_JWT_TOKEN;

  public JwtExpiredException() {
    super(errorCode);
  }

  public JwtExpiredException(String message) {
    super(errorCode, message);
  }
}
