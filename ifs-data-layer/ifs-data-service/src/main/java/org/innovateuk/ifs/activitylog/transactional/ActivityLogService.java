package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityType;

public interface ActivityLogService {

    void recordActivity(long applicationId, ActivityType activityType);

    void recordActivityByProjectId(long id, ActivityType type);

}
