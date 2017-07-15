package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.user.resource.AdminRoleType;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * DTO to transfer Invited User related information
 */
public class InviteUserResource {

    private UserResource invitedUser;

    private AdminRoleType adminRoleType;

    public InviteUserResource(UserResource invitedUser, AdminRoleType adminRoleType) {
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

    public AdminRoleType getAdminRoleType() {
        return adminRoleType;
    }

    public void setAdminRoleType(AdminRoleType adminRoleType) {
        this.adminRoleType = adminRoleType;
    }
}
