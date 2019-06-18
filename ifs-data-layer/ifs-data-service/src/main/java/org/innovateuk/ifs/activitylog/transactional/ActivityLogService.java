package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityType;

public interface ActivityLogService {

    void recordActivityByApplicationId(long applicationId, ActivityType activityType);

    void recordActivityByProjectId(long projectId, ActivityType activityType);

    void recordActivityByProjectIdAndOrganisationId(long projectId, long organisationId, ActivityType activityType);

    void recordDocumentActivityByProjectId(long projectId, ActivityType type, long documentConfigId);

    void recordQueryActivityByProjectFinanceId(long projectFinanceId, ActivityType type, long threadId);

}
