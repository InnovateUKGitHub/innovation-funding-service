package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionAssessment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface QuestionAssessmentRepository extends CrudRepository<QuestionAssessment, Long> {

    QuestionAssessment findByQuestionId(Long questionId);
    List<QuestionAssessment> findByQuestion_CompetitionId(Long questionId);
}