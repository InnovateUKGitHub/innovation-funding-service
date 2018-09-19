package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * DTO to transfer Invited User related information
 */
public class InviteUserResource {

    private UserResource invitedUser;

    private Role role;

    public InviteUserResource(UserResource invitedUser, Role role) {
        this.invitedUser = invitedUser;
        this.role = role;
    }

    public InviteUserResource(UserResource invitedUser) {
        this.invitedUser = invitedUser;
    }

    public InviteUserResource() {

    }

    public UserResource getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(UserResource invitedUser) {
        this.invitedUser = invitedUser;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
