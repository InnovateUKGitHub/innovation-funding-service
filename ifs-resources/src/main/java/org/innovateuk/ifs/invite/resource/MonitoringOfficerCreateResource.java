package org.innovateuk.ifs.invite.resource;

public class MonitoringOfficerCreateResource {

    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String emailAddress;

    public MonitoringOfficerCreateResource() {
        this.firstName = null;
        this.lastName = null;
        this.phoneNumber = null;
        this.emailAddress = null;
    }

    public MonitoringOfficerCreateResource(String firstName,
                                           String lastName,
                                           String phoneNumber,
                                           String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
