package org.innovateuk.ifs.management.roleprofile.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.user.resource.RoleProfileState;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @JsonIgnore
    public RoleProfileState getActiveRoleProfileState() {
        return RoleProfileState.ACTIVE;
    }

    @JsonIgnore
    public RoleProfileState getUnavailableRoleProfileState() {
        return RoleProfileState.UNAVAILABLE;
    }

    @JsonIgnore
    public RoleProfileState getDisabledRoleProfileState() {
        return RoleProfileState.DISABLED;
    }
}
