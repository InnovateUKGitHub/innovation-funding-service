package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.springframework.security.access.prepost.PreAuthorize;

public interface RemovePartnerNotificationService {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'REMOVE_PARTNER_ORGANISATION')")
    void sendNotifications(Project project, Organisation organisation);
}