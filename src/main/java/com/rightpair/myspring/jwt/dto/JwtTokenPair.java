package com.rightpair.myspring.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public record JwtTokenPair(
    String accessToken,
    String refreshToken
) {
}
