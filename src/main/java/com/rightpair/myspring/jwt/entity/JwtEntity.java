package com.rightpair.myspring.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@AllArgsConstructor
@Builder
@RedisHash(value = "jwt:refresh", timeToLive = 86400L)
public record JwtEntity(
    @Id
    Long memberId,
    String refreshToken
) {
}
