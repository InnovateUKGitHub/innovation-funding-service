package com.worth.ifs.email.resource;

/**
 *
 */
public class EmailAddressResource {

    private String emailAddress;
    private String name;

    public EmailAddressResource(String emailAddress, String name) {
        this.emailAddress = emailAddress;
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getName() {
        return name;
    }
}
