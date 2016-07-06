package com.worth.ifs.project.controller.form;

import com.worth.ifs.controller.BaseBindingResultTarget;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import java.util.Optional;

/**
 * Form to capture the posted details of the Monitoring Officer
 */
public class ProjectMonitoringOfficerForm extends BaseBindingResultTarget {

    @NotEmpty(message = "Please provide a first name")
    private String firstName;

    @NotEmpty(message = "Please provide a last name")
    private String lastName;

    @NotEmpty(message = "Please provide an email address")
    @Email(message = "Please provide a valid email address")
    private String emailAddress;

    @NotEmpty(message = "Please provide a phone number")
    @Pattern(regexp = "([0-9\\ +-])+",  message= "Please enter a valid phone number")
    private String phoneNumber;

    // for spring form binding
    public ProjectMonitoringOfficerForm() {
    }

    public ProjectMonitoringOfficerForm(Optional<MonitoringOfficerResource> existingMonitoringOfficer) {
        existingMonitoringOfficer.ifPresent(mo -> {
            setFirstName(mo.getFirstName());
            setLastName(mo.getLastName());
            setEmailAddress(mo.getEmail());
            setPhoneNumber(mo.getPhoneNumber());
        });
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
