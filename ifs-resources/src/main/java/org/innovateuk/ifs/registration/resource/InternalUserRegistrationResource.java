package org.innovateuk.ifs.registration.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * DTO for registering a User.
 */
public class InternalUserRegistrationResource {
    @NotEmpty(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.firstname.required}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.firstname.length.min}"),
            @Size(max = 70, message = "{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotEmpty(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.standard.lastname.required}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.lastname.length.min}"),
            @Size(max = 70, message = "{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    private String email;

    @NotEmpty(message = "{validation.standard.password.required}")
    @Size.List({
            @Size(min = 8, message = "{validation.standard.password.length.min}"),
    })
    private String password;

    private List<RoleResource> roles;

    public InternalUserRegistrationResource() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<RoleResource> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleResource> roles) {
        this.roles = roles;
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

    public UserResource toUserResource() {
        UserResource userResource = new UserResource();
        userResource.setFirstName(this.getFirstName());
        userResource.setLastName(this.getLastName());
        userResource.setPassword(this.getPassword());
        userResource.setEmail(this.getEmail());
        userResource.setRoles(this.getRoles());
        return userResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InternalUserRegistrationResource that = (InternalUserRegistrationResource) o;

        return new EqualsBuilder()
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(email, that.email)
                .append(password, that.password)
                .append(roles, that.roles)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(email)
                .append(password)
                .append(roles)
                .toHashCode();
    }
}
