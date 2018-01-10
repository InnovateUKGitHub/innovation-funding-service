package org.innovateuk.ifs.management.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.commons.validation.ValidationConstants;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class InviteNewAssessorsRowForm {


    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.name.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.name.length.min}"),
            @Size(max=70, message="{validation.standard.name.length.max}"),
    })
    @NotEmpty(message = "{validation.standard.name.required}")
    private String name;

    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.standard.email.format}")
    @Size(max = 254, message = "{validation.standard.email.length.max}")
    @NotEmpty(message = "{validation.inviteNewAssessorsForm.invites.email.required}")
    private String email;

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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InviteNewAssessorsRowForm that = (InviteNewAssessorsRowForm) o;

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
