package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompetitionOrganisationConfigRepository extends CrudRepository<CompetitionOrganisationConfig, Long> {

    Optional<CompetitionOrganisationConfig> findOneByCompetitionId(long competitionId);
}