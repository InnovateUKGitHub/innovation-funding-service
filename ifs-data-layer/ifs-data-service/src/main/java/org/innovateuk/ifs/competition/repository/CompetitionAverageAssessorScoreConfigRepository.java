package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionAverageAssessorScoreConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompetitionAverageAssessorScoreConfigRepository extends CrudRepository<CompetitionAverageAssessorScoreConfig, Long> {

    Optional<CompetitionAverageAssessorScoreConfig> findOneByCompetitionId(long competitionId);
}
