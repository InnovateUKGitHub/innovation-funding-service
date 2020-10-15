package org.innovateuk.ifs.management.externalrole.form;

import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.user.resource.Role;

@FieldRequiredIf(required = "organisation", argument = "coFunder", predicate = true, message = "{validation.invite.organisation.required}")
public class ExternalRoleForm {

    private String email;

    private Role role;

    private String organisation;

    public ExternalRoleForm() {
    }

    public ExternalRoleForm(String email, Role role) {
        this.email = email;
        this.role = role;
    }

    public ExternalRoleForm(String email, Role role, String organisation) {
        this.email = email;
        this.role = role;
        this.organisation = organisation;
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

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public boolean isCoFunder() {
        return Role.COFUNDER == role;
    }
}
