package org.innovateuk.ifs.application.forms.questions.team.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Form to invite a new application team member.
 */
public class ApplicationTeamForm {

    @NotBlank(message = "{validation.standard.name.required}")
    private String name;

    @NotBlank(message = "{validation.applicationteam.email.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.applicationteam.email.format}")
    @Size(max = 254, message = "{validation.applicationteam.email.required}")
    private String email;

    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.applicationteam.email.format}")
    @Size(max = 254, message = "{validation.applicationteam.email.required}")
    private String ktaEmail;

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

    public String getKtaEmail() {
        return ktaEmail;
    }

    public void setKtaEmail(String ktaEmail) {
        this.ktaEmail = ktaEmail;
    }
}
