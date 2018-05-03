package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface InterviewRepository extends ProcessRepository<Interview>, PagingAndSortingRepository<Interview, Long> {

    List<Interview> findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(long userId, long competitionId);

}