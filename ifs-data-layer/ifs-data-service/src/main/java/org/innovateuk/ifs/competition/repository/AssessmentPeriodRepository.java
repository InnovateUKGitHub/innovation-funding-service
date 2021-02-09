package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentPeriodRepository extends CrudRepository<AssessmentPeriod, Long> {

    List<AssessmentPeriod> findByCompetitionId(Long competitionId);

    void deleteByCompetitionId(Long competitionId);
}
