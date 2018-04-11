package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface InterviewAssignmentRepository extends ProcessRepository<InterviewAssignment>, PagingAndSortingRepository<InterviewAssignment, Long> {

    void deleteByTargetIdAndActivityStateState(long applicationId, State backingState);

    void deleteByTargetCompetitionIdAndActivityStateState(long competitionId, State backingState);

    Page<InterviewAssignment> findByTargetCompetitionIdAndActivityStateState(long competitionId, State backingState, Pageable pagable);

    List<InterviewAssignment> findByTargetCompetitionIdAndActivityStateState(long competitionId, State backingState);

    Page<InterviewAssignment> findByTargetCompetitionIdAndActivityStateStateNot(long competitionId, State backingState, Pageable pagable);

}
