package org.innovateuk.ifs.management.externalrole;

import org.innovateuk.ifs.user.resource.Role;

public class ExternalRoleForm {

    private String email;

    private Role role;

    public ExternalRoleForm(String email, Role role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
