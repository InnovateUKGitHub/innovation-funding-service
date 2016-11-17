package com.worth.ifs.assessment.repository;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentRepository extends ProcessRepository<Assessment>, PagingAndSortingRepository<Assessment, Long> {

    @Override
    Set<Assessment> findAll();

    List<Assessment> findAll(Iterable<Long> longs);

    Assessment findOneByParticipantId(Long participantId);

    List<Assessment> findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(Long userId, Long competitionId);
}