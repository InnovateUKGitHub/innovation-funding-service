
package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ActivityLogService {

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordActivityByApplicationId(long applicationId, ActivityType activityType);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordActivityByProjectId(long projectId, ActivityType activityType);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordActivityByProjectIdAndOrganisationId(long projectId, long organisationId, ActivityType activityType);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordActivityByProjectIdAndOrganisationIdAndAuthorId(long projectId, long organisationId, long authorId, ActivityType activityType);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordDocumentActivityByProjectId(long projectId, ActivityType type, long documentConfigId);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordQueryActivityByProjectFinanceId(long projectFinanceId, ActivityType type, long threadId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_ACTIVITY_LOG", description = "Only internal users can view activity log")
    ServiceResult<List<ActivityLogResource>> findByApplicationId(long applicationId);


}
