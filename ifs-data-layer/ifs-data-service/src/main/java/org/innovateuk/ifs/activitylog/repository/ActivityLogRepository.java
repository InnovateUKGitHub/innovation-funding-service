package org.innovateuk.ifs.activitylog.repository;

import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActivityLogRepository extends CrudRepository<ActivityLog, Long> {

    List<ActivityLog> findByApplicationId(long applicationId);
}
