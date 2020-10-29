package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectNotificationService {

    @SecuredBySpring(value = "INFORM_SUCCESSFUL_PROJECT_SETUP",
            description = "Comp admins and project finance users can inform applicants that their application moved to project setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> sendProjectSetupNotification(long applicationId);
}
