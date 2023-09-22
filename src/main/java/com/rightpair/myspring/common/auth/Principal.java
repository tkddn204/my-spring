package com.rightpair.myspring.common.auth;

import lombok.Builder;

@Builder
public record Principal(
    Long memberId,
    String accessToken
) {
}
