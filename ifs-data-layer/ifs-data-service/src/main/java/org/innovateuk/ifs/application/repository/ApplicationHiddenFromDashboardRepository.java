package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationHiddenFromDashboard;
import org.springframework.data.repository.Repository;

public interface ApplicationHiddenFromDashboardRepository extends Repository<ApplicationHiddenFromDashboard, Long> {

    ApplicationHiddenFromDashboard save(ApplicationHiddenFromDashboard deletedApplicationAudit);
}
