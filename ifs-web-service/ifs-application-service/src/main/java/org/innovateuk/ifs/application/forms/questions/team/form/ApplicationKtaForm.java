package org.innovateuk.ifs.application.forms.questions.team.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ApplicationKtaForm {

    @NotBlank(message = "{validation.applicationteam.email.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.applicationteam.email.format}")
    @Size(max = 254, message = "{validation.applicationteam.email.required}")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
