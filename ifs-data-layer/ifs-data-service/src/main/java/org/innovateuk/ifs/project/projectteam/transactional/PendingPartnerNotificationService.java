package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PendingPartnerNotificationService {

    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    void sendNotifications(PartnerOrganisation partnerOrganisation);
}