package com.rightpair.myspring.member.repository;

import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class MemberRepositoryTest extends TestSettings {

  @Autowired
  private MemberRepository memberRepository;

  @DisplayName("findbyEmail(Long id)를 호출할 때")
  @Nested
  class FindByEmailTest {
    @DisplayName("DB에 존재하는 사용자가 있을 때 DB에서 Member를 가져올 수 있어야 함")
    @Test
    void shouldGetMemberByEmail() {
      // given
      Member member = MemberTestFactory.createTestMember();
      memberRepository.save(member);

      // when
      Optional<Member> foundMember = memberRepository.findByEmail(member.getEmail());

      // then
      assertTrue(foundMember.isPresent());
      assertEquals(member.getId(), foundMember.get().getId());
      assertEquals(member.getEmail(), foundMember.get().getEmail());
    }


    @DisplayName("DB에 존재하지 않는 사용자를 검색했을 경우 비어있는 Optional<Member> 반환")
    @Test
    void shouldNotGetMemberByEmail() {
      // given
      String wrongEmail = "wrong@mail.zz";
      Member member = MemberTestFactory.createTestMember();
      memberRepository.save(member);

      // when
      Optional<Member> foundMember = memberRepository.findByEmail(wrongEmail);

      // then
      assertTrue(foundMember.isEmpty());
      assertThrows(NoSuchElementException.class, foundMember::get);
    }
  }

  @DisplayName("existsByEmail(String email)를 호출할 때")
  @Nested
  class ExistsByEmailTest {
    @DisplayName("DB에 존재하는 사용자를 검색할 때 true여야 함")
    @Test
    void shouldGetExistsByEmail() {
      // given
      Member member = MemberTestFactory.createTestMember();
      memberRepository.save(member);

      // when
      boolean foundMember = memberRepository.existsByEmail(member.getEmail());

      // then
      assertTrue(foundMember);
    }


    @DisplayName("DB에 없는 사용자를 검색할 때 false여야 함")
    @Test
    void shouldNotGetExistsByEmail() {
      // given
      String wrongEmail = "wrong@mail.zz";
      Member member = MemberTestFactory.createTestMember();
      memberRepository.save(member);

      // when
      boolean foundMember = memberRepository.existsByEmail(wrongEmail);

      // then
      assertFalse(foundMember);
    }
  }
}