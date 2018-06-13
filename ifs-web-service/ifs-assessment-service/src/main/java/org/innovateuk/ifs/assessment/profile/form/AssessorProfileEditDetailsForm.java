package org.innovateuk.ifs.assessment.profile.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.innovateuk.ifs.user.resource.Gender;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.innovateuk.ifs.commons.validation.PhoneNumberValidator.VALID_PHONE_NUMBER_8_20_DIGITS;

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

    @NotNull(message = "{validation.standard.gender.selectionrequired}")
    private Gender gender = Gender.NOT_STATED;

    @NotNull(message = "{validation.standard.ethnicity.selectionrequired}")
    private EthnicityResource ethnicity;

    @NotNull(message = "{validation.standard.disability.selectionrequired}")
    private Disability disability = Disability.NOT_STATED;

    @Valid
    private AddressResource addressForm = new AddressResource();

    @NotBlank(message = "{validation.standard.phonenumber.required}")
    @Pattern(regexp = VALID_PHONE_NUMBER_8_20_DIGITS,  message= "{validation.standard.phonenumber.format}")
    private String phoneNumber;

    public AssessorProfileEditDetailsForm() {
        this.ethnicity = new EthnicityResource();
        ethnicity.setId(7L);
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

    public Gender getGender() {
        return gender;
    }

    public EthnicityResource getEthnicity() {
        return ethnicity;
    }

    public Disability getDisability() {
        return disability;
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
                .append(gender, that.gender)
                .append(ethnicity, that.ethnicity)
                .append(disability, that.disability)
                .append(addressForm, that.addressForm)
                .append(phoneNumber, that.phoneNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(gender)
                .append(ethnicity)
                .append(disability)
                .append(addressForm)
                .append(phoneNumber)
                .toHashCode();
    }
}
