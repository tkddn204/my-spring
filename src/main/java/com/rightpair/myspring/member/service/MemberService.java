package com.rightpair.myspring.member.service;

import com.rightpair.myspring.member.dto.GetMemberDto;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.dto.LoginMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.exception.ExistedEmailException;
import com.rightpair.myspring.member.exception.InvalidPasswordException;
import com.rightpair.myspring.member.exception.MemberNotFoundException;
import com.rightpair.myspring.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public GetMemberDto.Response getMemberById(Long id) {
    Member member = memberRepository.findById(id)
        .orElseThrow(MemberNotFoundException::new);

    return GetMemberDto.Response.fromEntity(member);
  }

  @Transactional
  public JoinMemberDto.Response joinMember(JoinMemberDto.Request request) {
    if (memberRepository.existsByEmail(request.email())) {
      throw new ExistedEmailException();
    }

    Member member = memberRepository.save(
        Member.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .nickname(request.nickname())
            .build()
    );

    return JoinMemberDto.Response.fromEntity(member);
  }

  public LoginMemberDto.Response loginMember(LoginMemberDto.Request request) {
    Member member = memberRepository.findByEmail(request.email())
        .orElseThrow(MemberNotFoundException::new);

    validatePassword(request.password(), member.getPassword());

    return LoginMemberDto.Response.fromEntity(member);
  }

  private void validatePassword(String input, String saved) {
    if (!passwordEncoder.matches(input, saved)) {
      throw new InvalidPasswordException();
    }
  }

}
