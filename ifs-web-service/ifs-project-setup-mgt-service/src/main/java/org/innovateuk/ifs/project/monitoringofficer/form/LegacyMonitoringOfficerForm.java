package org.innovateuk.ifs.project.monitoringofficer.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Optional;

import static org.innovateuk.ifs.commons.validation.PhoneNumberValidator.VALID_PHONE_NUMBER;

/**
 * Form to capture the posted details of the Monitoring Officer
 */
public class LegacyMonitoringOfficerForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.firstname.length.min}"),
            @Size(max=70, message="{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotBlank(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.lastname.length.min}"),
            @Size(max=70, message="{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.standard.email.format}")
    @NotBlank(message = "{validation.invite.email.required}")
    @Size(max = 254, message = "{validation.standard.email.length.max}")
    private String emailAddress;

    @NotBlank(message = "{validation.standard.phonenumber.required}")
    @Pattern(regexp = VALID_PHONE_NUMBER,  message= "{validation.standard.phonenumber.format}")
    private String phoneNumber;

    // for spring form binding
    public LegacyMonitoringOfficerForm() {
    }

    public LegacyMonitoringOfficerForm(Optional<MonitoringOfficerResource> existingMonitoringOfficer) {
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
