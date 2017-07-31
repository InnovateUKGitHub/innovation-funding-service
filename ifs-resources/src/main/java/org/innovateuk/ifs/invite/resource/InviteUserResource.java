package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * DTO to transfer Invited User related information
 */
public class InviteUserResource {

    private UserResource invitedUser;

    private UserRoleType adminRoleType;

    public InviteUserResource(UserResource invitedUser, UserRoleType adminRoleType) {
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

    public UserRoleType getAdminRoleType() {
        return adminRoleType;
    }

    public void setAdminRoleType(UserRoleType adminRoleType) {
        this.adminRoleType = adminRoleType;
    }
}
