package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around invites for users.
 */
public interface InviteUserService {

    @PreAuthorize("hasPermission(#invitedUser, 'SAVE_USER_INVITE')")
    ServiceResult<Void> saveUserInvite(UserResource invitedUser, UserRoleType adminRoleType);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_EXISTNG_INVITE_FOR_HASH",
            description = "The System Registration user can get invite using hash to process registration")
    ServiceResult<RoleInviteResource> getInvite(String inviteHash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "CHECK_IF_USER_ALREADY_EXISTS",
            description = "The System Registration user can get status of invite using hash to process registration")
    ServiceResult<Boolean> checkExistingUser(String inviteHash);

    //@PostAuthorize("hasPermission(returnObject, 'READ')") - TODO - Add similar permissions
    ServiceResult<RoleInvitePageResource> findPendingInternalUserInvites(Pageable pageable);
}
