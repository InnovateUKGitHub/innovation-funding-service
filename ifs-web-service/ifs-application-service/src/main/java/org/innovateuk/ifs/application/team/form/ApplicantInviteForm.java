package org.innovateuk.ifs.application.team.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.Size;

/**
 * Form field model for the Applicant fields within the Add/Update Organisation views.
 */
public class ApplicantInviteForm extends BaseBindingResultTarget {

    @NotEmpty(message = "{validation.standard.name.required}")
    private String name;

    @NotEmpty(message = "{validation.invite.email.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.standard.email.format}")
    @Size(max = 254, message = "{validation.standard.email.length.max}")
    private String email;

    public ApplicantInviteForm() {
    }

    public ApplicantInviteForm(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicantInviteForm that = (ApplicantInviteForm) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(email)
                .toHashCode();
    }
}