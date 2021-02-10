package org.innovateuk.ifs.management.roleprofile.form;

import javax.validation.constraints.Size;
import org.innovateuk.ifs.user.resource.RoleProfileState;

import javax.validation.constraints.NotNull;

public class ChangeRoleProfileForm {

    @NotNull(message = "{validation.changeroleprofileform.role.required}")
    private RoleProfileState roleProfileState;

    @Size(max = 255, message = "{validation.field.too.many.characters}")
    private String unavailableReason;

    @Size(max = 255, message = "{validation.field.too.many.characters}")
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

    public RoleProfileState getRoleProfileState() {
        return roleProfileState;
    }

    public void setRoleProfileState(RoleProfileState roleProfileState) {
        this.roleProfileState = roleProfileState;
    }
}
