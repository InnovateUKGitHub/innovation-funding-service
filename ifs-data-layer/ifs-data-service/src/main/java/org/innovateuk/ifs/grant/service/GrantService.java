package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.commons.service.ServiceResult;

public interface GrantService {
    ServiceResult<Void> sendProject(final Long applicationId);
}

