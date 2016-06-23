package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.AssessmentFeedback;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentFeedbackRepository extends CrudRepository<AssessmentFeedback, Long> {

    @Override
    List<AssessmentFeedback> findAll();

}
