package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface GrantService {
    @NotSecured(value = "Anyone can send grant data", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> sendProject(final Long applicationId);
}

