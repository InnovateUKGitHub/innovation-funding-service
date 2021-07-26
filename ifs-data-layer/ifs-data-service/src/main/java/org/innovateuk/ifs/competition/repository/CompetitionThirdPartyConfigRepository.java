package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionThirdPartyConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompetitionThirdPartyConfigRepository extends CrudRepository<CompetitionThirdPartyConfig, Long> {

    Optional<CompetitionThirdPartyConfig> findOneByCompetitionId(long competitionId);
}
