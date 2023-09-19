package com.rightpair.myspring.common.error;

public record ExceptionResponse(
    ErrorCode errorCode,
    String message
) {
}
