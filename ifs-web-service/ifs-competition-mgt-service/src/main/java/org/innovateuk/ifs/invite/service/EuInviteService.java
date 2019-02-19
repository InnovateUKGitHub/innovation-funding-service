package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;
import org.innovateuk.ifs.invite.resource.EuContactResource;

import java.util.List;

public interface EuInviteService {

    ServiceResult<EuContactPageResource> getEuContactsByNotified(boolean notified,
                                                                 int pageIndex,
                                                                 int pageSize);
}
