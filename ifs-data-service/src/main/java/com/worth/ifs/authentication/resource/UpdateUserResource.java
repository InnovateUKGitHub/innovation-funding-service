package com.worth.ifs.authentication.resource;

/**
 * Represents a request to an Identity Provider to update an existing User record
 */
public class UpdateUserResource {

    private String password;

    /**
     * For JSON marshalling
     */
    public UpdateUserResource() {
    }

    public UpdateUserResource(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return password;
    }
}
