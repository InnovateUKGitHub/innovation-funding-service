package org.innovateuk.ifs.cofunder.repository;

import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CofunderAssignmentRepository extends ProcessRepository<CofunderAssignment>, PagingAndSortingRepository<CofunderAssignment, Long> {

    Optional<CofunderAssignment> findByParticipantIdAndTargetId(long userId, long applicationId);
    boolean existsByParticipantIdAndTargetId(long userId, long applicationId);

}
