package com.rightpair.myspring.common.error.exception;

import com.rightpair.myspring.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorcode;
  private final String message;

  public BusinessException(ErrorCode errorcode, String message) {
    super(errorcode.getValue());
    this.errorcode = errorcode;
    this.message = message;
  }

  public BusinessException(ErrorCode errorcode) {
    super(errorcode.getValue());
    this.errorcode = errorcode;
    this.message = errorcode.getValue();
  }
}
