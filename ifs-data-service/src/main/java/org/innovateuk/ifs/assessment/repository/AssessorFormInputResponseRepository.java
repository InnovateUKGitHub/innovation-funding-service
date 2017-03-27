package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessorFormInputResponseRepository extends CrudRepository<AssessorFormInputResponse, Long> {

    @Override
    List<AssessorFormInputResponse> findAll();

    List<AssessorFormInputResponse> findByAssessmentId(Long assessmentId);

    List<AssessorFormInputResponse> findByAssessmentIdAndFormInputQuestionId(Long assessmentId, Long questionId);

    AssessorFormInputResponse findByAssessmentIdAndFormInputId(Long assessmentId, Long formInputId);

    List<AssessorFormInputResponse> findByAssessmentTargetId(Long applicationId);

    List<AssessorFormInputResponse> findByAssessmentTargetIdAndFormInputQuestionId(long applicationId, long questionId);
}
