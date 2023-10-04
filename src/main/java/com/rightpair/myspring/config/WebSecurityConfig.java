package com.rightpair.myspring.config;

import com.rightpair.myspring.config.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  @Bean
  public SecurityFilterChain http(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable);

    http.requestCache(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(
        request -> request.requestMatchers(
//                AntPathRequestMatcher.antMatcher("/**"),
                AntPathRequestMatcher.antMatcher("/api/post/**"),
                AntPathRequestMatcher.antMatcher("/api/member/**"),
                AntPathRequestMatcher.antMatcher("/api/auth/refresh")
            ).permitAll()
            .requestMatchers(HttpMethod.POST, "/api/post").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/post/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/post/**").authenticated()
    );

    http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
