package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;

/**
 * A service for dealing with User Invites via the appropriate Rest services
 */
public interface InviteUserService {

    ServiceResult<Void> saveUserInvite(InviteUserResource inviteUserResource);
}