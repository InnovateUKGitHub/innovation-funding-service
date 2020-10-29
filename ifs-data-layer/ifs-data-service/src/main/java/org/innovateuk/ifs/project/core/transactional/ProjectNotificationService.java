package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectNotificationService {

    @SecuredBySpring(value = "INFORM_SUCCESSFUL_PROJECT_SETUP",
            description = "A System Maintenance User can inform applicants that their application moved to project setup")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<Void> sendProjectSetupNotification(long applicationId);
}
