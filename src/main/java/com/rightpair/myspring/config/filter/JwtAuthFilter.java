package com.rightpair.myspring.config.filter;

import com.rightpair.myspring.common.auth.Principal;
import com.rightpair.myspring.jwt.exception.JwtDeniedException;
import com.rightpair.myspring.jwt.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final static String JWT_TOKEN_PREFIX = "bearer ";

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

    boolean notExistedToken = !(accessToken != null && !accessToken.isBlank());
    if (notExistedToken) {
      filterChain.doFilter(request, response);
      return;
    }

    String extractedAccessToken = extractedToken(accessToken);
    Claims claims = jwtService.verifyToken(extractedAccessToken);
    request.setAttribute("memberId", Long.parseLong(claims.getSubject()));

    Authentication authentication = createAuthentication(extractedAccessToken, claims);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  private Authentication createAuthentication(String accessToken, Claims claims) {
    Principal principal = Principal.builder()
        .memberId(Long.parseLong(claims.getSubject()))
        .accessToken(accessToken)
        .build();
    return new UsernamePasswordAuthenticationToken(principal, accessToken, Collections.emptyList());
  }

  private String extractedToken(String token) {
    if (token.startsWith(JWT_TOKEN_PREFIX)) {
      return token.substring(JWT_TOKEN_PREFIX.length());
    }

    throw new JwtDeniedException();
  }
}
