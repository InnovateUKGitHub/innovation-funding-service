package org.innovateuk.ifs.activitylog.repository;

import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.springframework.data.repository.CrudRepository;

public interface ActivityLogRepository extends CrudRepository<ActivityLog, Long> {
}
