package com.rightpair.myspring.common.error;

import com.rightpair.myspring.common.error.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> businessException(BusinessException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(exception.getErrorcode(), exception.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> globalException(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(ErrorCode.INTERNAL_ERROR, exception.getMessage()));
  }
}
