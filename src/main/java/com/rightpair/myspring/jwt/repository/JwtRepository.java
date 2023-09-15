package com.rightpair.myspring.jwt.repository;

import com.rightpair.myspring.jwt.entity.JwtEntity;
import org.springframework.data.repository.CrudRepository;

public interface JwtRepository extends CrudRepository<JwtEntity, Long> {
}