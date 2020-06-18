package org.innovateuk.ifs.project.grants.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole;

import javax.validation.constraints.*;

public class GrantsSendInviteForm {

    @NotBlank(message = "{validation.grants.invite.firstname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.grants.invite.firstname.min}"),
            @Size(max=70, message="{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotBlank(message = "{validation.grants.invite.lastname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.grants.invite.lastname.min}"),
            @Size(max=70, message="{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.standard.email.format}")
    @NotBlank(message = "{validation.invite.email.required}")
    @Size(max = 254, message = "{validation.standard.email.length.max}")
    private String email;

    @NotNull(message = "{validation.grants.invite.role.required}")
    private GrantsInviteRole role;

    private Long organisationId;

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

    public GrantsInviteRole getRole() {
        return role;
    }

    public void setRole(GrantsInviteRole role) {
        this.role = role;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }
}
