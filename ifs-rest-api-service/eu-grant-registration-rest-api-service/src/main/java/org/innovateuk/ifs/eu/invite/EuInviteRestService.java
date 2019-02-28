package org.innovateuk.ifs.eu.invite;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuContactPageResource;

import java.util.List;


public interface EuInviteRestService {
    RestResult<EuContactPageResource> getEuContactsByNotified(boolean notified, Integer pageIndex, Integer pageSize);
    RestResult<Void> sendInvites(List<Long> ids);
}
