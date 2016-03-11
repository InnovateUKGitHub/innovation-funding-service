package com.worth.ifs.login;

import com.worth.ifs.validator.constraints.FieldMatch;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@FieldMatch(first = "password", second = "retypedPassword", message = "Passwords must match")
public class ResetPasswordForm {
    @NotEmpty(message = "Please enter your password")
    @Size(min = 6, max = 30, message = "Password size should be between 6 and 30 characters")
    private String password;

    @NotEmpty(message = "Please re-type your password")
    @Size(max = 30, message = "Password size should be between 6 and 30 characters")
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
