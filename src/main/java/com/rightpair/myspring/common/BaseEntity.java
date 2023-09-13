package com.rightpair.myspring.common;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  private LocalDateTime createdAt;

  private LocalDateTime lastModifiedAt;

  private Boolean isDeleted = false;
}
