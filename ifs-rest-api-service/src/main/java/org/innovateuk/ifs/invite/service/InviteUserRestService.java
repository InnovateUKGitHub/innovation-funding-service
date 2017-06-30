package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;

/**
 * REST service for Invite User
 */
public interface InviteUserRestService {
    RestResult<Void> saveUserInvite(InviteUserResource inviteUserResource);
    RestResult<Boolean> checkExistingUser(String inviteHash);
    RestResult<RoleInviteResource> getInvite(String inviteHash);
}



