package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * DTO to transfer Invited User related information
 */
public class InviteUserResource {

    private UserResource invitedUser;

    private Role adminRoleType;

    public InviteUserResource(UserResource invitedUser, Role adminRoleType) {
        this.invitedUser = invitedUser;
        this.adminRoleType = adminRoleType;
    }

    public InviteUserResource() {

    }

    public UserResource getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(UserResource invitedUser) {
        this.invitedUser = invitedUser;
    }

    public Role getAdminRoleType() {
        return adminRoleType;
    }

    public void setAdminRoleType(Role adminRoleType) {
        this.adminRoleType = adminRoleType;
    }
}
