package org.innovateuk.ifs.assessment.panel.repository;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentReviewRepository extends ProcessRepository<AssessmentReview>, PagingAndSortingRepository<AssessmentReview, Long> {
    List<AssessmentReview> findByTargetIdAndActivityStateState(long competitionId, State backingState);

    boolean existsByParticipantUserAndTarget(User user, Application target);
}