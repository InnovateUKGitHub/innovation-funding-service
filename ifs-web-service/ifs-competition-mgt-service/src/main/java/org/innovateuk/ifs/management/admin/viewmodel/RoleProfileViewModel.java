package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;

import static java.lang.String.format;
import static org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE;

public class RoleProfileViewModel {

    private RoleProfileStatusResource roleProfileStatus;

    private UserResource modifiedUser;

    public RoleProfileViewModel(RoleProfileStatusResource roleProfileStatus,
                                UserResource modifiedUser) {
        this.roleProfileStatus = roleProfileStatus;
        this.modifiedUser = modifiedUser;
    }

    public RoleProfileStatusResource getRoleProfileStatus() {
        return roleProfileStatus;
    }

    public void setRoleProfileStatus(RoleProfileStatusResource roleProfileStatus) {
        this.roleProfileStatus = roleProfileStatus;
    }

    public UserResource getModifiedUser() {
        return modifiedUser;
    }

    public void setModifiedUser(UserResource modifiedUser) {
        this.modifiedUser = modifiedUser;
    }

    /* view model logic. */
    public boolean displayStatusChange() {
        return !ACTIVE.equals(roleProfileStatus.getRoleProfileState());
    }

    public String modifiedUserDetails() {
        return format("%s, %s", this.modifiedUser.getName(), this.modifiedUser.getRolesString());
    }

    public String statusChangeReason() {
        return "The account has been made " + this.roleProfileStatus.getRoleProfileState().getDescription()
                + " because " + this.roleProfileStatus.getDescription();
    }

}
