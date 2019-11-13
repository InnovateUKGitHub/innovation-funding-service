package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.GrantProcessConfiguration;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GrantProcessConfigurationRepository extends CrudRepository<GrantProcessConfiguration, Long> {
    Optional<GrantProcessConfiguration> findByCompetitionId(Long competitionId);
}
