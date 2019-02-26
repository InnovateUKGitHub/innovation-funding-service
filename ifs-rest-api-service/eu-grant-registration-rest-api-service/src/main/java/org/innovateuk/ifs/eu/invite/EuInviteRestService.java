package org.innovateuk.ifs.eu.invite;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuGrantPageResource;

import java.util.List;
import java.util.UUID;


public interface EuInviteRestService {
    RestResult<EuGrantPageResource> getEuGrantsByContactNotified(boolean notified, Integer pageIndex, Integer pageSize);
    RestResult<Void> sendInvites(List<UUID> ids);
}
