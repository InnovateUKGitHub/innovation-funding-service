package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionApplicationConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompetitionApplicationConfigRepository extends CrudRepository<CompetitionApplicationConfig, Long> {
    Optional<CompetitionApplicationConfig> findOneByCompetitionId(long competitionId);
}
