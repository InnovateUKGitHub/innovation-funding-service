package org.innovateuk.ifs.assessment.period.repository;

import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentPeriodRepository extends CrudRepository<AssessmentPeriod, Long> {

    List<AssessmentPeriod> findByCompetitionId(long competitionId);

    Page<AssessmentPeriod> findByCompetitionId(long competitionId, Pageable pageable);

    Optional<AssessmentPeriod> findFirstByCompetitionId(Long competitionId);

    void deleteByCompetitionId(Long competitionId);
}
