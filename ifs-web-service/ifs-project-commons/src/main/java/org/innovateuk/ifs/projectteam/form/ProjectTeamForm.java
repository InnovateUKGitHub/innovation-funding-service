package org.innovateuk.ifs.projectteam.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Form to invite a new project team member.
 */
public class ProjectTeamForm {

    @NotBlank(message = "{validation.standard.name.required}")
    private String name;

    @NotBlank(message = "{validation.projectteam.email.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.projectteam.email.format}")
    @Size(max = 254, message = "{validation.projectteam.email.required}")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
