package com.rightpair.myspring.utils;

import com.github.javafaker.Faker;
import com.rightpair.myspring.member.entity.Member;
import com.rightpair.myspring.post.entity.Post;

import java.util.ArrayList;
import java.util.List;

public class PostTestFactory {

  private static final Faker faker = new Faker();

  public static Post createTestPost(Long memberId) {
    return Post.builder()
        .member(Member.builder().id(memberId).build())
        .title(faker.lorem().sentence(faker.random().nextInt(3, 7)))
        .content(faker.lorem().paragraph())
        .build();
  }

  public static Post createTestPostWithId(Long memberId) {
    return Post.builder()
        .id(faker.number().randomNumber())
        .member(Member.builder().id(memberId).build())
        .title(faker.lorem().sentence(faker.random().nextInt(3, 7)))
        .content(faker.lorem().paragraph())
        .build();
  }

  public static List<Post> createTestPostList() {
    List<Post> postList = new ArrayList<>();

    // 랜덤한 5개 ID 고름
    List<Long> memberList = MemberTestFactory.createTestMemberIdList();

    // 비둘기집 원리에 의해 Post 중 적어도 1개는 여러 개의 ID를 가질 것임
    for (int i = 0; i < 10; i++)
      postList.add(createTestPost(
          memberList.get(faker.random().nextInt(0, memberList.size() - 1))
      ));

    return postList;
  }
}
