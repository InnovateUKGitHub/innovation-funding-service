package org.innovateuk.ifs.login.form;

import org.innovateuk.ifs.validator.constraints.FieldMatch;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@FieldMatch(first = "password", second = "retypedPassword", message = "{validation.standard.password.match}")
public class ResetPasswordForm {
    @NotEmpty(message = "{validation.standard.password.required}")
    @Size(min = 8, message = "{validation.standard.password.length.min}")
    private String password;

    @NotEmpty(message = "{validation.standard.password.required}")
    @Size(min = 8, message = "{validation.standard.password.length.min}")
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
