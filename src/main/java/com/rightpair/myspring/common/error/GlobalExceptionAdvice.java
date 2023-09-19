package com.rightpair.myspring.common.error;

import com.rightpair.myspring.common.error.exception.BusinessException;
import com.rightpair.myspring.common.error.exception.JwtSecurityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> bindException(BindException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ErrorCode.BINDING_ERROR,
            exception.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> jwtException(JwtSecurityException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(exception.getErrorCode(), exception.getMessage()));
  }

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
