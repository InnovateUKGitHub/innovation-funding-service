package com.worth.ifs.authentication.resource;

/**
 * Represents the return result from creating a User
 */
public class CreateUserResponse {

    private String uniqueId;

    /**
     * For JSON marshalling
     */
    public CreateUserResponse() {

    }

    public CreateUserResponse(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUniqueId() {
        return uniqueId;
    }
}
