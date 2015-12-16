package com.worth.ifs.login;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * This object is used for the loginForm. When the form is submitted the data is
 * injected into a LoginForm instance, so it is easy to use and you don't need to
 * read all the request attributes to get to the form data. It is also use when
 * you want to prefill a form.
 */
public class LoginForm {

    @NotEmpty(message = "Please enter your e-mail address")
    @Email(message = "Please enter a valid e-mail address")
    private String email;
    @NotEmpty(message = "Please enter your password")
    private String password;
    private String actionUrl = "/login";

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

}
