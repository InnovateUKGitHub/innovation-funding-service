package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;

import static java.lang.String.format;
import static org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE;

public class RoleProfileViewModel {

    private RoleProfileStatusResource roleProfileStatus;

    private UserResource modifiedUser;

    private boolean changeRoleStatusEnabled;

    public RoleProfileViewModel(RoleProfileStatusResource roleProfileStatus,
                                UserResource modifiedUser,
                                boolean changeRoleStatusEnabled) {
        this.roleProfileStatus = roleProfileStatus;
        this.modifiedUser = modifiedUser;
        this.changeRoleStatusEnabled = changeRoleStatusEnabled;
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

    public boolean isChangeRoleStatusEnabled() {
        return changeRoleStatusEnabled;
    }

    public void setChangeRoleStatusEnabled(boolean changeRoleStatusEnabled) {
        this.changeRoleStatusEnabled = changeRoleStatusEnabled;
    }

    /* view model logic. */
    public boolean displayStatusChange() {
        return !ACTIVE.equals(roleProfileStatus.getRoleProfileState());
    }

    public String modifiedUserDetails() {
        return format("%s, %s", this.modifiedUser.getName(), this.modifiedUser.getRolesString());
    }
}
