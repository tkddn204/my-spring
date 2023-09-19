package com.rightpair.myspring.common.error.exception;

import com.rightpair.myspring.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class JwtSecurityException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String message;

  public JwtSecurityException(ErrorCode errorCode, String message) {
    super(errorCode.getValue());
    this.errorCode = errorCode;
    this.message = message;
  }

  public JwtSecurityException(ErrorCode errorCode) {
    super(errorCode.getValue());
    this.errorCode = errorCode;
    this.message = errorCode.getValue();
  }
}
