package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationExternalConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationExternalConfigRepository extends CrudRepository<ApplicationExternalConfig, Long> {
    Optional<ApplicationExternalConfig> findOneByApplicationId(long competitionId);
}

