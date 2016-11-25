package com.worth.ifs.login.form;

import com.worth.ifs.validator.constraints.FieldMatch;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@FieldMatch(first = "password", second = "retypedPassword", message = "Passwords must match")
public class ResetPasswordForm {
    @NotEmpty(message = "{validation.standard.password.required}")
    @Size(min = 10, max = 30, message = "{validation.standard.password.length.range}")
    private String password;

    @NotEmpty(message = "{validation.standard.password.required}")
    @Size(min = 10, max = 30, message = "{validation.standard.password.length.range}")
    private String retypedPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }
}
