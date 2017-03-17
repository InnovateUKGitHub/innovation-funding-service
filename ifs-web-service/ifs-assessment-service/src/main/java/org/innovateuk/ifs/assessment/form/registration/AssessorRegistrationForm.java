package org.innovateuk.ifs.assessment.form.registration;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.validator.constraints.FieldMatch;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Form field model for the Assessor Registration
 */
@FieldMatch(first = "password", second = "retypedPassword", message = "{validation.standard.password.match}")
public class AssessorRegistrationForm extends BaseBindingResultTarget {

    @NotEmpty(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} -]*", message = "{validation.standard.firstname.required}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.firstname.length.min}"),
            @Size(max=70, message="{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotEmpty(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} -]*", message = "{validation.standard.lastname.required}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.lastname.length.min}"),
            @Size(max=70, message="{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @NotEmpty(message = "{validation.standard.password.required}")
    @Size.List ({
            @Size(min=10, message="{validation.standard.password.length.min}"),
            @Size(max=30, message="{validation.standard.password.length.max}"),
    })
    private String password;

    @NotEmpty(message = "{validation.standard.retypedpassword.required}")
    @Size.List ({
            @Size(min=10, message="{validation.standard.password.length.min}"),
            @Size(max=30, message="{validation.standard.password.length.max}"),
    })
    private String retypedPassword;

    @Valid
    private AddressForm addressForm = new AddressForm();

    @NotEmpty(message = "{validation.standard.phonenumber.required}")
    @Size.List ({
            @Size(min=8, message="{validation.standard.phonenumber.length.min}"),
            @Size(max=20, message="{validation.standard.phonenumber.length.max}")
    })
    @Pattern(regexp = "([0-9\\ +-])+",  message= "{validation.standard.phonenumber.format}")
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }

    public AddressForm getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressForm addressForm) {
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

        AssessorRegistrationForm that = (AssessorRegistrationForm) o;

        return new EqualsBuilder()
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(password, that.password)
                .append(retypedPassword, that.retypedPassword)
                .append(addressForm, that.addressForm)
                .append(phoneNumber, that.phoneNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(password)
                .append(retypedPassword)
                .append(addressForm)
                .append(phoneNumber)
                .toHashCode();
    }
}
