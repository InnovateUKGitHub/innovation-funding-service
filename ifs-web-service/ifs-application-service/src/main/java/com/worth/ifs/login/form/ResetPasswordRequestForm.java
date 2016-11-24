package com.worth.ifs.login.form;

import com.worth.ifs.commons.validation.ValidationConstants;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * This object is used for the loginForm. When the form is submitted the data is
 * injected into a LoginForm instance, so it is easy to use and you don't need to
 * read all the request attributes to get to the form data. It is also use when
 * you want to prefill a form.
 */
public class ResetPasswordRequestForm {

    @NotEmpty(message="{validation.standard.email.required}")
    @Email(message="{validation.standard.email.format}")
    @Size(max = 256, message = "{validation.standard.email.length.max}")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
