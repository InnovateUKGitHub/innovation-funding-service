package com.worth.ifs.finance.repository;

import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FinanceRowRepository<T> {
    List<T> findByTargetId(@Param("targetId") Long targetId);
    T findOneByTargetIdAndNameAndQuestionId(Long targetId, String name, Long questionId);
    List<T> findByTargetIdAndNameAndQuestionId(Long targetId, String name, Long questionId);
    List<T> findByTargetIdAndQuestionId(Long targetId, Long questionId);
}
