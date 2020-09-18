package org.innovateuk.ifs.management.admin.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.commons.validation.constraints.FieldComparison;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.user.resource.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Form to capture details of the edited User
 */
@FieldComparison(
        firstField = "emailAddress",
        secondField = "ktpRole",
        message = "{validation.kta.invite.email.invalid}",
        predicate = EmailAddressValidator.KtpPredicateProvider.class
)
@FieldComparison(
        firstField = "emailAddress",
        secondField = "ktpRole",
        message = "{validation.standard.email.format}",
        predicate = EmailAddressValidator.NonKtpPredicateProvider.class
)
public class EditUserForm extends BaseBindingResultTarget {

    public interface InternalUserFieldsGroup {
    }
    @NotBlank(message = "{validation.standard.firstname.required}", groups = InternalUserFieldsGroup.class)
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.invalid}", groups = InternalUserFieldsGroup.class)
    @Size.List ({
            @Size(min=2, message="{validation.invite.firstname.length.min}", groups = InternalUserFieldsGroup.class),
            @Size(max=70, message="{validation.invite.firstname.length.max}", groups = InternalUserFieldsGroup.class),
    })
    private String firstName;

    @NotBlank(message = "{validation.standard.lastname.required}", groups = InternalUserFieldsGroup.class)
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.invalid}", groups = InternalUserFieldsGroup.class)
    @Size.List ({
            @Size(min=2, message="{validation.invite.lastname.length.min}", groups = InternalUserFieldsGroup.class),
            @Size(max=70, message="{validation.invite.lastname.length.max}", groups = InternalUserFieldsGroup.class),
    })
    private String lastName;

    @NotBlank(message = "{validation.standard.emailinternal.required}")
    @Size(max = 254, message = "{validation.invite.email.length.max}")
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

    public boolean isKtpRole() {
        return Role.KNOWLEDGE_TRANSFER_ADVISER == role;
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
