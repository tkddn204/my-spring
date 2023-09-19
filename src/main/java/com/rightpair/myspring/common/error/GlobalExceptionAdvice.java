package com.rightpair.myspring.common.error;

import com.rightpair.myspring.common.error.exception.JwtSecurityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

  @ExceptionHandler
  public ResponseEntity<ExceptionResponse> bindException(BindException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionResponse(ErrorCode.BINDING_ERROR,
            exception.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
  }

  @ExceptionHandler
  public ResponseEntity<ExceptionResponse> jwtException(JwtSecurityException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionResponse(exception.getErrorCode(), exception.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity<ExceptionResponse> globalException(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionResponse(ErrorCode.INTERNAL_ERROR, exception.getMessage()));
  }
}
