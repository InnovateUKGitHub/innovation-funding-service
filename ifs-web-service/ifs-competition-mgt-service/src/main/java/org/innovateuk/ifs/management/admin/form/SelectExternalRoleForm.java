package org.innovateuk.ifs.management.admin.form;

import org.innovateuk.ifs.user.resource.Role;

import javax.validation.constraints.NotNull;

public class SelectExternalRoleForm {

    @NotNull(message = "{validation.role.required}")
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
