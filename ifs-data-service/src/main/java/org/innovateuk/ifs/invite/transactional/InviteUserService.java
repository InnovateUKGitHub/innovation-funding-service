package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

/**
 * TODO - Desc here
 */
public interface InviteUserService {

    ServiceResult<Void> saveUserInvite(UserResource inviteUser, UserRoleType role);
}
