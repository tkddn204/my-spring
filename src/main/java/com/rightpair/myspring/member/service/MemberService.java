package com.rightpair.myspring.member.service;

import com.rightpair.myspring.member.dto.GetMemberDto;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.repository.MemberRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public GetMemberDto.Response getMemberById(Long id) {
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

    return Optional.ofNullable(member)
        .map(GetMemberDto.Response::fromEntity)
        .orElseThrow(() -> new RuntimeException("getMemberById Response DTO 변환에 실패했습니다."));
  }

  @Transactional
  public JoinMemberDto.Response joinMember(JoinMemberDto.Request request) {
    if (memberRepository.existsByEmail(request.email())) {
      throw new EntityExistsException("이미 존재하는 이메일입니다.");
    }

    Member member = memberRepository.save(
        Member.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .nickname(request.nickname())
            .build()
    );

    return Optional.of(member)
        .map(JoinMemberDto.Response::fromEntity)
        .orElseThrow(() -> new RuntimeException("createMember Response DTO 변환에 실패했습니다."));
  }
}
