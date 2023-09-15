package com.rightpair.myspring.jwt.entity;

public record JwtEntity(
    String token,
    JwtType jwtType
) {
}
