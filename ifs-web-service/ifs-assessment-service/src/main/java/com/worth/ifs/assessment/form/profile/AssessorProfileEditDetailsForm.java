package com.worth.ifs.assessment.form.profile;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.controller.BaseBindingResultTarget;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.Gender;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Form field model to edit Assessor Profile Details
 */
public class AssessorProfileEditDetailsForm extends BaseBindingResultTarget {

    @NotEmpty(message = "{validation.standard.title.selectionrequired}")
    @Size(max = 5, message = "{validation.standard.title.length.max}")
    @Pattern(regexp = "^(Mr|Miss|Mrs|Ms|Dr)$", message = "{validation.standard.title.format}")
    private String title;

    @NotEmpty(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} -]*", message = "{validation.standard.firstname.required}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.firstname.length.min}"),
            @Size(max = 70, message = "{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotEmpty(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} -]*", message = "{validation.standard.lastname.required}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.lastname.length.min}"),
            @Size(max = 70, message = "{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @NotNull(message = "{validation.standard.gender.selectionrequired}")
    private Gender gender;

    @NotNull(message = "{validation.standard.ethnicity.selectionrequired}")
    private EthnicityResource ethnicity;

    @NotNull(message = "{validation.standard.disability.selectionrequired}")
    private Disability disability;

    @Valid
    private AddressResource addressForm = new AddressResource();

    @NotEmpty(message = "{validation.standard.phonenumber.required}")
    @Size.List({
            @Size(min = 8, message = "{validation.standard.phonenumber.length.min}"),
            @Size(max = 20, message = "{validation.standard.phonenumber.length.max}")
    })
    @Pattern(regexp = "([0-9\\ +-])+", message = "{validation.standard.phonenumber.format}")
    private String phoneNumber;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public EthnicityResource getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(EthnicityResource ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Disability getDisability() {
        return disability;
    }

    public void setDisability(Disability disability) {
        this.disability = disability;
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
                .append(title, that.title)
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
                .append(title)
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
