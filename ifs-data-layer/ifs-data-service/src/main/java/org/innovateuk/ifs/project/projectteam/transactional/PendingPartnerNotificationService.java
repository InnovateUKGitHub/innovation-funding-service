package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;

public interface PendingPartnerNotificationService {

    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    void sendNotifications(PartnerOrganisation partnerOrganisation);
}