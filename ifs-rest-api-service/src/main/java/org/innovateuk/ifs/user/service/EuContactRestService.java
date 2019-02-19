package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;
import org.innovateuk.ifs.invite.resource.EuContactResource;

import java.awt.print.Pageable;
import java.util.List;

public interface EuContactRestService {

    RestResult<EuContactPageResource> getEuContactsByNotified(boolean notified, Integer pageIndex, Integer pageSize);
}
