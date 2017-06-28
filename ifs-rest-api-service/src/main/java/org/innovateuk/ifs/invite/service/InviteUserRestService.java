package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;

/**
 * REST service for Invite User
 */
public interface InviteUserRestService {
    RestResult<Void> saveUserInvite(InviteUserResource inviteUserResource);
}



