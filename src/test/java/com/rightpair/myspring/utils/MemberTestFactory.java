package com.rightpair.myspring.utils;

import com.github.javafaker.Faker;
import com.rightpair.myspring.member.entity.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberTestFactory {

  private static final Faker faker = new Faker();
  private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public static Member createTestMember() {
    return Member.builder()
        .email(faker.internet().emailAddress())
        .password(passwordEncoder.encode(faker.random().hex(12)))
        .nickname(faker.name().firstName())
        .build();
  }

  public static Member createUnEncodedTestMember() {
    return Member.builder()
        .email(faker.internet().emailAddress())
        .password(faker.random().hex(12))
        .nickname(faker.name().firstName())
        .build();
  }
}
