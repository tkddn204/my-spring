package com.rightpair.myspring.post.entity;

import com.rightpair.myspring.common.BaseEntity;
import com.rightpair.myspring.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_id", updatable = false)
  private Long id;

  @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", insertable = false, updatable = false)
  private Member member;

  @NotBlank
  private String title;

  private String content;
}
