package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationExpressionOfInterestConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationExpressionOfInterestConfigRepository extends CrudRepository<ApplicationExpressionOfInterestConfig, Long> {
    Optional<ApplicationExpressionOfInterestConfig> findOneByApplicationId(long applicationId);
}

