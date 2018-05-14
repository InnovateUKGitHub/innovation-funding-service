package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.interview.domain.InterviewAssignmentResponseOutcome;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface InterviewAssignmentResponseOutcomeRepository extends CrudRepository<InterviewAssignmentResponseOutcome, Long> {
}
