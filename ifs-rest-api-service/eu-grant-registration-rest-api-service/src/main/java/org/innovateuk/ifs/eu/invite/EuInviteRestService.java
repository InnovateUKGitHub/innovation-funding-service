package org.innovateuk.ifs.eu.invite;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuContactPageResource;


public interface EuInviteRestService {
    RestResult<EuContactPageResource> getEuContactsByNotified(boolean notified, Integer pageIndex, Integer pageSize);
}
