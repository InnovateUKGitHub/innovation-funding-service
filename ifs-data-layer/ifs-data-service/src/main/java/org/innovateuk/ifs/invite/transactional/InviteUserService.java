package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secured service providing operations around invites for users.
 */
public interface InviteUserService {

    @PreAuthorize("hasPermission(#invitedUser, 'SAVE_USER_INVITE')")
    ServiceResult<Void> saveUserInvite(UserResource invitedUser, Role role);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_EXISTING_INVITE_FOR_HASH", description = "The System Registration user can get invite using hash to process registration")
    ServiceResult<RoleInviteResource> getInvite(String inviteHash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "CHECK_IF_USER_ALREADY_EXISTS", description = "The System Registration user can get status of invite using hash to process registration")
    ServiceResult<Boolean> checkExistingUser(String inviteHash);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<RoleInvitePageResource> findPendingInternalUserInvites(String filter, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('support', 'ifs_administrator')")
    @SecuredBySpring(value = "READ_ALL_EXTERNAL_USER_INVITES", description = "Only the support user or IFS Admin can access external user invites")
    ServiceResult<List<ExternalInviteResource>> findExternalInvites(String searchString, SearchCategory searchCategory);

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "RESEND_INTERNAL_USER_INVITES", description = "Only the IFS Administrators can resend internal user invites")
    ServiceResult<Void> resendInternalUserInvite(long inviteId);
}