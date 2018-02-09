package org.innovateuk.ifs.assessment.interview.repository;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentInterviewPanelRepository extends ProcessRepository<AssessmentInterviewPanel>, PagingAndSortingRepository<AssessmentInterviewPanel, Long> {
}
