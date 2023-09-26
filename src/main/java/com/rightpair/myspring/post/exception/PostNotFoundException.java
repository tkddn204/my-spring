package com.rightpair.myspring.post.exception;

import com.rightpair.myspring.common.error.ErrorCode;
import com.rightpair.myspring.common.error.exception.BusinessException;

public class PostNotFoundException extends BusinessException {

  private final static ErrorCode errorCode = ErrorCode.POST_NOT_FOUND;

  public PostNotFoundException() {
    super(errorCode);
  }
}
