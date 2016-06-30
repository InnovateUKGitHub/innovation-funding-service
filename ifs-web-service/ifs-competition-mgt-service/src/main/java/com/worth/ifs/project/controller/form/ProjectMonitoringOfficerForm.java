package com.worth.ifs.project.controller.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form to capture the posted details of the Monitoring Officer
 */
public class ProjectMonitoringOfficerForm {

    @NotEmpty(message = "Please provide a first name")
    private String firstName;

    @NotEmpty(message = "Please provide a last name")
    private String lastName;

    @NotEmpty(message = "Please provide an email address")
    @Email(message = "Please provide a valid email address")
    private String emailAddress;

    @NotEmpty(message = "Please provide a phone number")
    private String phoneNumber;

    // for spring form binding
    public ProjectMonitoringOfficerForm() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
