package com.worth.ifs.registration.form;

import com.worth.ifs.util.FormUtil;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Object to store the data that is used from the Resend Email Verification form.
 */
public class ResendEmailVerificationForm {

    @NotEmpty(message = "Please enter your e-mail address")
    @Email(regexp = FormUtil.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "Please enter a valid e-mail address")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}