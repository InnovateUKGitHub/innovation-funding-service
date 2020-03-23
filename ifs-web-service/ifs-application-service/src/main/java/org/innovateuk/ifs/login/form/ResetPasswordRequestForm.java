package org.innovateuk.ifs.login.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * This object is used for the loginForm. When the form is submitted the data is
 * injected into a LoginForm instance, so it is easy to use and you don't need to
 * read all the request attributes to get to the form data. It is also use when
 * you want to prefill a form.
 */
public class ResetPasswordRequestForm {

    @NotBlank(message="{validation.standard.email.required}")
    @Email(message="{validation.standard.email.format}")
    @Size(max = 254, message = "{validation.standard.email.length.max}")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
