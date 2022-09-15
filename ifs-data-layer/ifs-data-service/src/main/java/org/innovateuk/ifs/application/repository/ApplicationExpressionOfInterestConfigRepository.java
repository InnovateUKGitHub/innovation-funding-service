package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationExpressionOfInterestConfig;
import org.innovateuk.ifs.application.domain.ApplicationHiddenFromDashboard;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationExpressionOfInterestConfigRepository extends CrudRepository<ApplicationExpressionOfInterestConfig, Long> {
    Optional<ApplicationExpressionOfInterestConfig> findOneByApplicationId(long applicationId);
    List<ApplicationExpressionOfInterestConfig> findByApplicationId(long applicationId);
    void deleteByApplicationId(long applicationId);
}

