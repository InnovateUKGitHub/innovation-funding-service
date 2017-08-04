package org.innovateuk.ifs.registration.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Form field model for registration of internal user
 */
public class InternalUserRegistrationForm extends BaseBindingResultTarget {

    @NotEmpty(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.firstname.length.min}"),
            @Size(max=70, message="{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotEmpty(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.lastname.length.min}"),
            @Size(max=70, message="{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @NotEmpty(message = "{validation.standard.password.required}")
    @Size.List ({
            @Size(min=8, message="{validation.standard.password.length.min}"),
    })
    private String password;

    public InternalUserRegistrationForm() {
    }

    public InternalUserRegistrationForm(String firstName, String lastName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InternalUserRegistrationForm that = (InternalUserRegistrationForm) o;

        return new EqualsBuilder()
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(password, that.password)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(password)
                .toHashCode();
    }
}
