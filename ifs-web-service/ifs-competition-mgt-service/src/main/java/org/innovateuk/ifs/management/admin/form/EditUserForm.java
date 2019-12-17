package org.innovateuk.ifs.management.admin.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.user.resource.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Form to capture details of the edited User
 */
public class EditUserForm extends BaseBindingResultTarget {

    public interface InternalUserFieldsGroup {
    }
    @NotBlank(message = "{validation.standard.firstname.required}", groups = InternalUserFieldsGroup.class)
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.invalid}", groups = InternalUserFieldsGroup.class)
    @Size.List ({
            @Size(min=2, message="{validation.standard.firstname.length.min}", groups = InternalUserFieldsGroup.class),
            @Size(max=70, message="{validation.standard.firstname.length.max}", groups = InternalUserFieldsGroup.class),
    })
    private String firstName;

    @NotBlank(message = "{validation.standard.lastname.required}", groups = InternalUserFieldsGroup.class)
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.invalid}", groups = InternalUserFieldsGroup.class)
    @Size.List ({
            @Size(min=2, message="{validation.standard.lastname.length.min}", groups = InternalUserFieldsGroup.class),
            @Size(max=70, message="{validation.standard.lastname.length.max}", groups = InternalUserFieldsGroup.class),
    })
    private String lastName;

    @NotBlank(message = "{validation.standard.emailinternal.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.standard.email.format}")
    @Size(max = 254, message = "{validation.standard.email.length.max}")
    private String email;

    private Role role;

    public EditUserForm() {
        // for spring form binding
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EditUserForm form = (EditUserForm) o;

        return new EqualsBuilder()
                .append(firstName, form.firstName)
                .append(lastName, form.lastName)
                .append(email, form.email)
                .append(role, form.role)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(email)
                .append(role)
                .toHashCode();
    }
}
