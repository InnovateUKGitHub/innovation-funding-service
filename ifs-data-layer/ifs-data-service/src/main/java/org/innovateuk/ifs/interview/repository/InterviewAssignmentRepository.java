package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface InterviewAssignmentRepository extends ProcessRepository<InterviewAssignment>, PagingAndSortingRepository<InterviewAssignment, Long> {
}
