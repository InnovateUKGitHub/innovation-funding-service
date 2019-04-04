package org.innovateuk.ifs.invite.resource;

public class CreateMonitoringOfficerResource {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;

    public CreateMonitoringOfficerResource() {
    }
    public CreateMonitoringOfficerResource(String firstName,
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
