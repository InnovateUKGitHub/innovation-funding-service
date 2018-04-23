package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface InterviewAssignmentRepository extends ProcessRepository<InterviewAssignment>, PagingAndSortingRepository<InterviewAssignment, Long> {

    void deleteByTargetIdAndActivityState(long applicationId, InterviewAssignmentState state);

    void deleteByTargetCompetitionIdAndActivityState(long competitionId, InterviewAssignmentState state);

    Page<InterviewAssignment> findByTargetCompetitionIdAndActivityState(long competitionId, InterviewAssignmentState state, Pageable pagable);

    List<InterviewAssignment> findByTargetCompetitionIdAndActivityState(long competitionId, InterviewAssignmentState state);

    Page<InterviewAssignment> findByTargetCompetitionIdAndActivityStateNot(long competitionId, InterviewAssignmentState state, Pageable pagable);

    boolean existsByTargetIdAndActivityStateIn(long applicationId, List<InterviewAssignmentState> states);

    int countByTargetCompetitionIdAndActivityStateIn(long competitionId, Set<InterviewAssignmentState> states);
}
