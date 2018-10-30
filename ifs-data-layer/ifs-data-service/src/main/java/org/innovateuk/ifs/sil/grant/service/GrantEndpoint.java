package org.innovateuk.ifs.sil.grant.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.grant.resource.Grant;

/**
 * Sent project data to grant monitoring service.
 */
public interface GrantEndpoint {
    ServiceResult<Void> send(Grant grant);
}
