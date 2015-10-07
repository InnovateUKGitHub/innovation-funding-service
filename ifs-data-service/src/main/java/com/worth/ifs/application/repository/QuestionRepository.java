package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.Question;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface QuestionRepository extends PagingAndSortingRepository<Question, Long> {
    List<Question> findAll();
    List<Question> findByCompetitionId(@Param("competitionId") Long competitionId);
}