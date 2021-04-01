package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationHiddenFromDashboard;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ApplicationHiddenFromDashboardRepository extends Repository<ApplicationHiddenFromDashboard, Long> {

    List<ApplicationHiddenFromDashboard> findByApplicationId(long applicationId);
    ApplicationHiddenFromDashboard save(ApplicationHiddenFromDashboard deletedApplicationAudit);
    void deleteByApplicationId(long applicationId);
}
