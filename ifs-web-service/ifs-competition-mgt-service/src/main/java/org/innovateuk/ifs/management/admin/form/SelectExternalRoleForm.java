package org.innovateuk.ifs.management.admin.form;

import javax.validation.constraints.NotNull;

public class SelectExternalRoleForm {

    @NotNull(message = "{validation.role.required}")
    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
