package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationExternalConfig;
import org.innovateuk.ifs.application.domain.ApplicationPreRegConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationPreRegConfigRepository extends CrudRepository<ApplicationPreRegConfig, Long> {
    Optional<ApplicationPreRegConfig> findOneByApplicationId(long applicationId);
}

