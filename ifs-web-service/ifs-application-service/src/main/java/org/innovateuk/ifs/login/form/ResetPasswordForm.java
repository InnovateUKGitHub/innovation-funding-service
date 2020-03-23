package org.innovateuk.ifs.login.form;

import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ResetPasswordForm {
    @NotBlank(message = "{validation.standard.password.required}")
    @Size(min = 8, message = "{validation.standard.password.length.min}")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
