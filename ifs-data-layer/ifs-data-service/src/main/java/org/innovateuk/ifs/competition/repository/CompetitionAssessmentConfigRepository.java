package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompetitionAssessmentConfigRepository extends CrudRepository<CompetitionAssessmentConfig, Long> {

    Optional<CompetitionAssessmentConfig> findOneByCompetitionId(long competitionId);
}
