package com.worth.ifs.authentication.resource;

/**
 * Represents a request to an Identity Provider to update an existing User record
 */
public class UpdateUserResource {

    private String title;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;

    public UpdateUserResource(String title, String firstName, String lastName, String emailAddress, String phoneNumber) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
