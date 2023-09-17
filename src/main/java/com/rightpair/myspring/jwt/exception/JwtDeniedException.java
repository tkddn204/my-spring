package com.rightpair.myspring.jwt.exception;

import com.rightpair.myspring.common.error.ErrorCode;
import com.rightpair.myspring.common.error.exception.JwtSecurityException;

public class JwtDeniedException extends JwtSecurityException {

  private final static ErrorCode errorCode = ErrorCode.DENIED_JWT_TOKEN;

  public JwtDeniedException() {
    super(errorCode);
  }

  public JwtDeniedException(String message) {
    super(errorCode, message);
  }
}
