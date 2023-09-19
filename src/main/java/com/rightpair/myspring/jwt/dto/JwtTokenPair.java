package com.rightpair.myspring.jwt.dto;

import lombok.Builder;

@Builder
public record JwtTokenPair(
    String accessToken,
    String refreshToken
) {
}
