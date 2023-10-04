package com.rightpair.myspring.post.exception;

import com.rightpair.myspring.common.error.ErrorCode;
import com.rightpair.myspring.common.error.exception.BusinessException;

public class PostPermissionDeniedException extends BusinessException {

  private final static ErrorCode errorCode = ErrorCode.POST_PERMISSION_DENIED;

  public PostPermissionDeniedException() {
    super(errorCode);
  }
}
