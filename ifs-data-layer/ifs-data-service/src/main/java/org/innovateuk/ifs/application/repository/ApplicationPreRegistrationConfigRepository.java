package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationPreRegistrationConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationPreRegistrationConfigRepository extends CrudRepository<ApplicationPreRegistrationConfig, Long> {
    Optional<ApplicationPreRegistrationConfig> findOneByApplicationId(long applicationId);
}

