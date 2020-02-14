package org.innovateuk.ifs.management.admin.form;

import javax.validation.constraints.NotNull;

public class ChangeRoleProfileForm {

    @NotNull(message = "{validation.changeroleprofileform.role.required}")
    private String roleProfileState;

    private String unavailableReason;

    private String disabledReason;

    public String getUnavailableReason() {
        return unavailableReason;
    }

    public void setUnavailableReason(String unavailableReason) {
        this.unavailableReason = unavailableReason;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    public String getRoleProfileState() {
        return roleProfileState;
    }

    public void setRoleProfileState(String roleProfileState) {
        this.roleProfileState = roleProfileState;
    }

}
