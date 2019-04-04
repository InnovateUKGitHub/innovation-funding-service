package org.innovateuk.ifs.project.monitoringofficer.form;

public class MonitoringOfficerCreateForm {

    private String emailAddress;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    public MonitoringOfficerCreateForm() {

    }

    public MonitoringOfficerCreateForm(String emailAddress,
                                       String firstName,
                                       String lastName,
                                       String phoneNumber) {
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
