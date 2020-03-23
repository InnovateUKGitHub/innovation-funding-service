package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.validation.constraints.NotBlank;
import org.innovateuk.ifs.user.resource.Role;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DTO to transfer User information whilst editing
 */
public class EditUserResource {

    private Long userId;

    @NotBlank(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.required}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.firstname.length.min}"),
            @Size(max=70, message="{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotBlank(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.required}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.lastname.length.min}"),
            @Size(max=70, message="{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    private Role userRoleType;

    public EditUserResource() {

    }

    public EditUserResource(Long userId, String firstName, String lastName, Role userRoleType) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRoleType = userRoleType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Role getUserRoleType() {
        return userRoleType;
    }

    public void setUserRoleType(Role userRole) {
        this.userRoleType = userRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EditUserResource that = (EditUserResource) o;

        return new EqualsBuilder()
                .append(userId, that.userId)
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(userRoleType, that.userRoleType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(firstName)
                .append(lastName)
                .append(userRoleType)
                .toHashCode();
    }
}
