package com.worth.ifs.project.controller.form;

import com.worth.ifs.commons.validation.ValidationConstants;
import com.worth.ifs.controller.BaseBindingResultTarget;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.Optional;

/**
 * Form to capture the posted details of the Monitoring Officer
 */
public class ProjectMonitoringOfficerForm extends BaseBindingResultTarget {

    @NotEmpty(message = "Please enter a first name")
    private String firstName;

    @NotEmpty(message = "Please enter a last name")
    private String lastName;

    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "Please enter a valid email address")
    @NotEmpty(message = "Please enter your email")
    @Size(max = 256, message = "Your email address has a maximum length of 256 characters")
    private String emailAddress;

    @NotEmpty(message = "Please enter a phone number")
    @Size.List ({
            @Size(min=8, message="Input for your phone number has a minimum length of 8 characters"),
            @Size(max=20, message="Input for your phone number has a maximum length of 20 characters")
    })
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
