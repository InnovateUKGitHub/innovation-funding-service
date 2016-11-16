package com.worth.ifs.login;

import com.worth.ifs.user.resource.UserRoleType;
import org.hibernate.validator.constraints.NotEmpty;

public class RoleSelectionForm {

    @NotEmpty(message = "{validation.standard.role.required}")
    private UserRoleType selectedRole;

    public UserRoleType getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(UserRoleType selectedRole) {
        this.selectedRole = selectedRole;
    }
}
