package com.rightpair.myspring.post.repository;

import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.member.repository.MemberRepository;
import com.rightpair.myspring.post.entity.Post;
import com.rightpair.myspring.utils.MemberTestFactory;
import com.rightpair.myspring.utils.PostTestFactory;
import com.rightpair.myspring.utils.TestSettings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRepositoryTest extends TestSettings {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PostRepository postRepository;

  private List<Post> postList;

  @BeforeAll
  public void beforeAll() {
    Member member = MemberTestFactory.createTestMember();
    Member savedMember = memberRepository.save(member);
    postList = PostTestFactory.createTestPostList();
    postRepository.saveAll(postList.stream()
            .peek(post -> post.setMember(savedMember)).toList());
  }

  @DisplayName("페이지를 설정해서 PostList를 가져올 수 있다.")
  @ValueSource(ints = {1, 3, 5, 7, 10})
  @ParameterizedTest
  void shouldSuccessToGetPostList(int size) {
    // given
    Pageable pageable = Pageable.ofSize(size);

    // when
    Page<Post> response = postRepository.findAllByOrderByCreatedAtDesc(pageable);

    // then
    int expected = postList.size() / size;
    if (postList.size() % size > 0) expected++;
    assertEquals(expected, response.getTotalPages());
  }
}