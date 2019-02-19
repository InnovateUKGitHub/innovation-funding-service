package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;
import org.innovateuk.ifs.user.service.EuContactRestService;
import org.innovateuk.ifs.invite.resource.EuContactResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EuInviteServiceImpl implements EuInviteService {

    @Autowired
    private EuContactRestService euContactRestService;

    @Override
    public ServiceResult<EuContactPageResource> getEuContactsByNotified(boolean notified,
                                                                        int pageIndex,
                                                                        int pageSize) {
        return euContactRestService.getEuContactsByNotified(notified,
                                                            pageIndex,
                                                            pageSize).toServiceResult();
    }
}

