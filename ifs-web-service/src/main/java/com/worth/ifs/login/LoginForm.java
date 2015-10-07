package com.worth.ifs.login;

/**
 * This object is used for the loginForm. When the form is submitted the data is
 * injected into a LoginForm instance, so it is easy to use and you don't need to
 * read all the request attributes to get to the form data. It is also use when
 * you want to prefill a form.
 */
public class LoginForm {
    private String email;
    private String password;


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
}
