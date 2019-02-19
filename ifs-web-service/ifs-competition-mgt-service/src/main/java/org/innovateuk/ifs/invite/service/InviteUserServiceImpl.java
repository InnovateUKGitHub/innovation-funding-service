package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service for dealing with User Invites via the appropriate Rest services
 */
@Service
public class InviteUserServiceImpl implements InviteUserService {

    @Autowired
    private InviteUserRestService inviteUserRestService;

    @Override
    public ServiceResult<Void> saveUserInvite (InviteUserResource inviteUserResource) {
        return inviteUserRestService.saveUserInvite(inviteUserResource).toServiceResult();
    }
}

