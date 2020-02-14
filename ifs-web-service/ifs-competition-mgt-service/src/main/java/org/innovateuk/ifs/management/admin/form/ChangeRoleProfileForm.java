package org.innovateuk.ifs.management.admin.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class ChangeRoleProfileForm {

    @NotNull(message = "{validation.changeroleprofileform.role.required}")
    private String roleProfileState;

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

    public String getRoleProfileState() {
        return roleProfileState;
    }

    public void setRoleProfileState(String roleProfileState) {
        this.roleProfileState = roleProfileState;
    }

}
