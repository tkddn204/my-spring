package com.rightpair.myspring.common.error;

public record ErrorResponse(
    ErrorCode errorCode,
    String message
) {
}
