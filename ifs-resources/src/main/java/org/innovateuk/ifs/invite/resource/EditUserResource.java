package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

/**
 * DTO to transfer User information whilst editing
 */
public class EditUserResource {

    private UserResource userToEdit;

    private UserRoleType userRoleType;

    public EditUserResource() {

    }

    public EditUserResource(UserResource userToEdit, UserRoleType userRoleType) {
        this.userToEdit = userToEdit;
        this.userRoleType = userRoleType;
    }

    public UserResource getUserToEdit() {
        return userToEdit;
    }

    public void setUserToEdit(UserResource userToEdit) {
        this.userToEdit = userToEdit;
    }

    public UserRoleType getUserRoleType() {
        return userRoleType;
    }

    public void setUserRoleType(UserRoleType userRoleType) {
        this.userRoleType = userRoleType;
    }
}
