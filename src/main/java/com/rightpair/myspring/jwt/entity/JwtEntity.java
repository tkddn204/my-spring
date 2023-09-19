package com.rightpair.myspring.jwt.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Builder
@RedisHash(value = "jwt:refresh", timeToLive = 86400L)
public record JwtEntity(
    @Id
    Long memberId,
    String refreshToken
) {
}
