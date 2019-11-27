package org.innovateuk.ifs.grant.repository;

import org.innovateuk.ifs.grant.domain.GrantProcessConfiguration;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GrantProcessConfigurationRepository extends CrudRepository<GrantProcessConfiguration, Long> {
    Optional<GrantProcessConfiguration> findByCompetitionId(Long competitionId);
}
