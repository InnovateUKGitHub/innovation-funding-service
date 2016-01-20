package com.worth.ifs.authentication.resource;

/**
 * Represents a request to an Identity Provider to create a new User record
 */
public class CreateUserResource {

    private String emailAddress;
    private String password;

    /**
     * For JSON marshalling
     */
    public CreateUserResource() {
    }

    public CreateUserResource(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }
}
