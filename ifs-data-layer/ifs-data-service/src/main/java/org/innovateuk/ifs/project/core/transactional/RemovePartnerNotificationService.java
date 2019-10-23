package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.springframework.security.access.prepost.PreAuthorize;

public interface RemovePartnerNotificationService {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.PartnerOrganisationResource', 'REMOVE_PARTNER_ORGANISATION')")
    ServiceResult<Void> sendNotifications(long projectId, Organisation organisation);
}