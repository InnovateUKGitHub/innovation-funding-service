package org.innovateuk.ifs.management.admin.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.constraints.FieldComparison;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.user.resource.Role;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Form to capture the posted details of the newly invited user
 */
@FieldRequiredIf(required = "organisation", argument = "coFunder", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "emailAddress", argument = "ktpRole", predicate = true, message = "{validation.kta.invite.email.required}")
@FieldRequiredIf(required = "emailAddress", argument = "ktpRole", predicate = false, message = "{validation.invite.email.required}")
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
public class InviteUserForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.invite.firstname.length.min}"),
            @Size(max=70, message="{validation.invite.firstname.length.max}"),
    })
    private String firstName;

    @NotBlank(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.invite.lastname.length.min}"),
            @Size(max=70, message="{validation.invite.lastname.length.max}"),
    })
    private String lastName;

    @Size(max = 254, message = "{validation.invite.email.length.max}")
    private String emailAddress;

    private Role role;

    private String organisation;

    public InviteUserForm() {
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public boolean isKtpRole() {
        return Role.KNOWLEDGE_TRANSFER_ADVISER == role;
    }

    public boolean isCoFunder() {
        return Role.COFUNDER == role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InviteUserForm form = (InviteUserForm) o;

        return new EqualsBuilder()
                .append(firstName, form.firstName)
                .append(lastName, form.lastName)
                .append(emailAddress, form.emailAddress)
                .append(role, form.role)
                .append(organisation, form.organisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(emailAddress)
                .append(role)
                .append(organisation)
                .toHashCode();
    }
}
