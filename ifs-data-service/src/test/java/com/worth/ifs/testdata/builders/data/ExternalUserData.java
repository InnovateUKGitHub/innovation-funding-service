package com.worth.ifs.testdata.builders.data;

/**
 * Running data context for generating Applicants
 */
public class ExternalUserData extends BaseUserData {

    private String firstName;
    private String lastName;
    private String emailAddress;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
