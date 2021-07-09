package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionExternalConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompetitionExternalConfigRepository extends CrudRepository<CompetitionExternalConfig, Long> {
    Optional<CompetitionExternalConfig> findOneByCompetitionId(long competitionId);
}
