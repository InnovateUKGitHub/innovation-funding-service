package org.innovateuk.ifs.project.projectteam.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProjectPartnerInviteForm {
    @NotBlank(message = "{validation.applicationteam.organisation.name.required}")
    private String organisationName;

    @NotBlank(message = "{validation.standard.name.required}")
    private String userName;

    @NotBlank(message = "{validation.applicationteam.email.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.applicationteam.email.format}")
    @Size(max = 254, message = "{validation.applicationteam.email.required}")
    private String email;

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
