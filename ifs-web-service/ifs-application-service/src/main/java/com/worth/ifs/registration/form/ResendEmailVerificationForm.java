package com.worth.ifs.registration.form;

import com.worth.ifs.commons.validation.ValidationConstants;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Object to store the data that is used from the Resend Email Verification form.
 */
public class ResendEmailVerificationForm {

    @NotEmpty(message = "{validation.standard.email.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.standard.email.format}")
    @Size(max = 256, message = "{validation.standard.email.length.max}")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}