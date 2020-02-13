package org.innovateuk.ifs.management.admin.form;

import javax.validation.constraints.NotNull;

public class ChangeRoleProfileForm {

    @NotNull(message = "{validation.changeroleprofileform.role.required}")
    private String roleProfileState;

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRoleProfileState() {
        return roleProfileState;
    }

    public void setRoleProfileState(String roleProfileState) {
        this.roleProfileState = roleProfileState;
    }
}
