package org.innovateuk.ifs.project.monitoringofficer.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.Size;

/**
 * Form to capture Monitoring Officer search by email details
 */
public class MonitoringOfficerSearchByEmailForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.invite.email.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.standard.email.format}")
    @Size(max = 254, message = "{validation.standard.email.length.max}")
    private String emailAddress;

    // for spring form binding
    public MonitoringOfficerSearchByEmailForm() {
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

}
