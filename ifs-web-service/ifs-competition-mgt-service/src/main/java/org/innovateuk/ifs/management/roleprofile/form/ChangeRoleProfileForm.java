package org.innovateuk.ifs.management.roleprofile.form;

import org.hibernate.validator.constraints.Length;
import org.innovateuk.ifs.user.resource.RoleProfileState;

import javax.validation.constraints.NotNull;

public class ChangeRoleProfileForm {

    @NotNull(message = "{validation.changeroleprofileform.role.required}")
    private RoleProfileState roleProfileState;

    @Length(max = 255, message = "{validation.field.too.many.characters}")
    private String unavailableReason;

    @Length(max = 255, message = "{validation.field.too.many.characters}")
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
