package org.innovateuk.ifs.project.form.validation;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.group.GroupSequenceProvider;
import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Class to abstract invite validation
 */
@GroupSequenceProvider(ValidateInviteSequenceProvider.class)
public abstract class ValidateInviteForm extends BaseBindingResultTarget {


    public abstract boolean inviteRequired();

    @NotNull(groups = ValidateInviteChecks.class, message = "{validation.project.invite.name.required}")
    @NotBlank(groups = ValidateInviteChecks.class, message = "{validation.project.invite.name.required}")
    private String name;

    @NotNull(groups = ValidateInviteChecks.class, message = "{validation.project.invite.email.required}")
    @NotBlank(groups = ValidateInviteChecks.class, message = "{validation.project.invite.email.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message= "{validation.project.invite.email.invalid}", groups = ValidateInviteChecks.class)
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
}