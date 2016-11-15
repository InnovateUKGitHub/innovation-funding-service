package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.AssessmentScoreRow;
import com.worth.ifs.application.domain.QuestionAssessment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentScoreRowRepository extends CrudRepository<AssessmentScoreRow, Long> {

    List<AssessmentScoreRow> findByQuestionAssessment_Question_CompetitionId(Long competitionId);

}