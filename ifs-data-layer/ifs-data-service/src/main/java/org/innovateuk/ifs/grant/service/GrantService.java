package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface GrantService {
    @NotSecured(value = "Only called by scheduled process", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> sendProject(final Long applicationId);

    @NotSecured(value = "Only called by scheduled process", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> sendReadyProjects();
}

