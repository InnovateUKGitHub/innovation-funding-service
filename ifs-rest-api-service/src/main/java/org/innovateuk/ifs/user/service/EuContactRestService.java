package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;

/**
 * Interface for CRUD operations on {@link EuContactPageResource} related data.
 */

public interface EuContactRestService {
    RestResult<EuContactPageResource> getEuContactsByNotified(boolean notified, Integer pageIndex, Integer pageSize);
}
