package com.rightpair.myspring.common.exception;

public record ExceptionResponse(
    ErrorCode errorCode,
    String message
) {
}
