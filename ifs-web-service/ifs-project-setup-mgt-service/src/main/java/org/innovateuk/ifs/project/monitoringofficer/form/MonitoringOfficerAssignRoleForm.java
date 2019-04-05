package org.innovateuk.ifs.project.monitoringofficer.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.Pattern;

import static org.innovateuk.ifs.commons.validation.PhoneNumberValidator.VALID_PHONE_NUMBER;

/**
 * Form to capture data for assign role of Monitoring Officer to existing user
 */
public class MonitoringOfficerAssignRoleForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.standard.phonenumber.required}")
    @Pattern(regexp = VALID_PHONE_NUMBER,  message= "{validation.standard.phonenumber.format}")
    private String phoneNumber;

    // for spring form binding
    public MonitoringOfficerAssignRoleForm() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
