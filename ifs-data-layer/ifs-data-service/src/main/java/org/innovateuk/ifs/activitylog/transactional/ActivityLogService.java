package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityType;
import org.innovateuk.ifs.commons.security.NotSecured;

public interface ActivityLogService {

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordActivityByApplicationId(long applicationId, ActivityType activityType);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordActivityByProjectId(long projectId, ActivityType activityType);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordActivityByProjectIdAndOrganisationId(long projectId, long organisationId, ActivityType activityType);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordDocumentActivityByProjectId(long projectId, ActivityType type, long documentConfigId);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordQueryActivityByProjectFinanceId(long projectFinanceId, ActivityType type, long threadId);
}
