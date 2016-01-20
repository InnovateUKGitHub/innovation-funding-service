package com.worth.ifs.profile;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This object is used for the editing of user details. When the form is submitted the data is
 * injected into a UserDetailsForm instance, so it is easy to use and you don't need to
 * read all the request attributes to get to the form data.
 */
public class UserDetailsForm {
    private String email;

    @NotEmpty(message = "Please select a title")
    @Size(max = 5, message = "Last name input has a maximum input of 5 characters")
    private String title;

    @NotEmpty(message = "Please enter a first name")
    @Pattern(regexp = "[\\p{L} ]*", message = "Please enter a first name")
    @Length(min=2, max = 70)
    private String firstName;

    @NotEmpty(message = "Please enter a last name")
    @Pattern(regexp = "[\\p{L} ]*", message = "Please enter a last name")
    @Length(min=2, max = 70)
    private String lastName;

    @NotEmpty(message = "Please enter a phone number")
    @Length(min=6, max = 20, message = "Input for your phone number has a maximum length of 20 characters")
    @Pattern(regexp = "([0-9\\ +-])+",  message= "Please enter a valid phone number")
    private String phoneNumber;

    private String actionUrl;

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
}
