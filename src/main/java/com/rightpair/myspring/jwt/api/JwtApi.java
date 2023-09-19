package com.rightpair.myspring.jwt.api;

import com.rightpair.myspring.jwt.dto.RefreshTokenDto;
import com.rightpair.myspring.jwt.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class JwtApi {
  private final JwtService jwtService;

  @PostMapping("/api/auth/refresh")
  public ResponseEntity<RefreshTokenDto.Response> refreshAccessToken(
      @Valid @RequestBody RefreshTokenDto.RefreshRequest request
  ) {
    RefreshTokenDto.Response response = jwtService.refreshAccessToken(
        RefreshTokenDto.Request.from(request)
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
  }
}
