package org.innovateuk.ifs.assessment.interview.repository;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterview;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentInterviewRepository extends ProcessRepository<AssessmentInterview>, PagingAndSortingRepository<AssessmentInterview, Long> {
}