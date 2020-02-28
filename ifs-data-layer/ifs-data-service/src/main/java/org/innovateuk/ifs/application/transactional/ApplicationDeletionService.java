package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationUserCompositeId;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Security annotated interface for {@ApplicationServiceImpl}.
 */
public interface ApplicationDeletionService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'DELETE_APPLICATION')")
    ServiceResult<Void> deleteApplication(long applicationId);

    @PreAuthorize("hasPermission(#id, 'HIDE_APPLICATION')")
    ServiceResult<Void> hideApplicationFromDashboard(ApplicationUserCompositeId id);
}
