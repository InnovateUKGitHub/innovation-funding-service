package com.worth.ifs.authentication.resource;

/**
 * Represents a request to an Identity Provider to create a new User record
 */
public class CreateUserResource {

    private String title;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;
    private String phoneNumber;

    /**
     * For JSON marshalling
     */
    public CreateUserResource() {
    }

    public CreateUserResource(String title, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.password = password;
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

    public String getPassword() {
        return password;
    }
}
