package com.rightpair.myspring.utils;

import com.github.javafaker.Faker;
import com.rightpair.myspring.member.entity.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.IntStream;

public class MemberTestFactory {

  private static final Faker faker = new Faker();
  private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public static Member createTestMemberWithId() {
    return Member.builder()
        .id(faker.number().randomNumber())
        .email(faker.internet().emailAddress())
        .password(passwordEncoder.encode(faker.random().hex(12)))
        .nickname(faker.name().firstName())
        .build();
  }

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

  public static Member createTestMemberFromPassword(String password) {
    return Member.builder()
        .email(faker.internet().emailAddress())
        .password(passwordEncoder.encode(password))
        .nickname(faker.name().firstName())
        .build();
  }

  public static List<Long> createTestMemberIdList() {
    return IntStream.range(0, 5)
        .mapToObj(i -> faker.number().randomNumber())
        .toList();
  }
}
