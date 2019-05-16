package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

/**
 * Security annotated interface for {@ApplicationServiceImpl}.
 */
public interface ApplicationDashboardService {

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'CAN_VIEW_OWN_DASHBOARD')")
    ServiceResult<ApplicantDashboardResource> getApplicantDashboard(@P("userId")long userId);

}
