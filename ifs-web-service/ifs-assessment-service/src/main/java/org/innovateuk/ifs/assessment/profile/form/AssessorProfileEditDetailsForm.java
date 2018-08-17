package org.innovateuk.ifs.assessment.profile.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.innovateuk.ifs.commons.validation.PhoneNumberValidator.VALID_PHONE_NUMBER;

/**
 * Form field model to edit Assessor Profile Details
 */
public class AssessorProfileEditDetailsForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.invalid}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.firstname.length.min}"),
            @Size(max = 70, message = "{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotBlank(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.invalid}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.lastname.length.min}"),
            @Size(max = 70, message = "{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @Valid
    private AddressResource addressForm = new AddressResource();

    @NotBlank(message = "{validation.standard.phonenumber.required}")
    @Pattern(regexp = VALID_PHONE_NUMBER,  message= "{validation.standard.phonenumber.format}")
    private String phoneNumber;

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

    public AddressResource getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressResource addressForm) {
        this.addressForm = addressForm;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileEditDetailsForm that = (AssessorProfileEditDetailsForm) o;

        return new EqualsBuilder()
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(addressForm, that.addressForm)
                .append(phoneNumber, that.phoneNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(addressForm)
                .append(phoneNumber)
                .toHashCode();
    }
}