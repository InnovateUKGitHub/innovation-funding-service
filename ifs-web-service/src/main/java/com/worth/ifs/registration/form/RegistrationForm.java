package com.worth.ifs.registration.form;

import com.worth.ifs.validator.constraints.FieldMatch;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * a
 * This object is used for the account registration form. When the form is submitted the data is
 * injected into a RegistrationForm instance, so it is easy to use and you don't need to
 * read all the request attributes to get to the form data.
 */

@FieldMatch(first = "password", second = "retypedPassword", message = "Passwords must match")
public class RegistrationForm {
    @Email(message = "Please enter a valid email address")
    @NotEmpty(message = "Please enter your email")
    @Size(max = 256, message = "Input for your email address has a maximum length of 256 characters")
    private String email;

    @NotEmpty(message = "Please enter your password")
    @Size(min = 6, max = 30, message = "Password size should be between 6 and 30 characters")
    private String password;

    @NotEmpty(message = "Please re-type your password")
    @Size(max = 30, message = "Password size should be between 6 and 30 characters")
    private String retypedPassword;

    @NotEmpty(message = "Please select a title")
    @Size(max = 5, message = "Last name input has a maximum input of 5 characters")
    private String title;

    @NotEmpty(message = "Please enter a first name")
    @Pattern(regexp = "[\\p{L} -]*", message = "Please enter a first name")
    @Length(min=2, max = 70)
    private String firstName;

    @NotEmpty(message = "Please enter a last name")
    @Pattern(regexp = "[\\p{L} -]*", message = "Please enter a last name")
    @Length(min=2, max = 70)
    private String lastName;

    @NotEmpty(message = "Please enter a phone number")
    @Length(min=6, max = 20, message = "Input for your phone number has a maximum length of 20 characters")
    @Pattern(regexp = "([0-9\\ +-])+",  message= "Please enter a valid phone number")
    private String phoneNumber;

    @NotBlank(message = "In order to register an account you have to agree to the Terms and Conditions")
    private String termsAndConditions;

    private String actionUrl;

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

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
}
