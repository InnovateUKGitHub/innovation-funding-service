package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.security.access.prepost.PreAuthorize;

public interface MonitoringOfficerReviewNotificationService {
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_MONITORING_OFFICER')")
    ServiceResult<Void> sendDocumentReviewNotification(User monitoringOfficer, long projectId);
}
