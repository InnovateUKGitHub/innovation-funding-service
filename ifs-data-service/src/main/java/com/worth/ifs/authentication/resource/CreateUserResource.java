package com.worth.ifs.authentication.resource;

/**
 * Represents a request to an Identity Provider to create a new User record
 */
public class CreateUserResource {

    private String emailAddress;
    private String plainTextPassword;

    /**
     * For JSON marshalling
     */
    public CreateUserResource() {
    }

    public CreateUserResource(String emailAddress, String plainTextPassword) {
        this.emailAddress = emailAddress;
        this.plainTextPassword = plainTextPassword;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPlainTextPassword() {
        return plainTextPassword;
    }
}
