package org.innovateuk.ifs.registration.resource;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.user.resource.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * DTO for registering a User.
 */
public class UserRegistrationResource extends UserProfileBaseResource{

    @NotEmpty(message = "{validation.standard.password.required}")
    @Size.List({
            @Size(min = 8, message = "{validation.standard.password.length.min}"),
    })
    private String password;

    private List<RoleResource> roles;

    public UserRegistrationResource() {
    }

    public UserRegistrationResource(Title title, String firstName, String lastName, String phoneNumber, Gender gender, Disability disability, EthnicityResource ethnicity, String password, AddressResource address) {
        setTitle(title);
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
        setGender(gender);
        setDisability(disability);
        setEthnicity(ethnicity);
        this.password = password;
        setAddress(address);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserRegistrationResource that = (UserRegistrationResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(password, that.password)
                .append(roles, that.roles)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(password)
                .append(roles)
                .toHashCode();
    }

    public UserResource toUserResource() {
        UserResource userResource = new UserResource();
        userResource.setTitle(this.getTitle());
        userResource.setFirstName(this.getFirstName());
        userResource.setLastName(this.getLastName());
        userResource.setPhoneNumber(this.getPhoneNumber());
        userResource.setGender(this.getGender());
        userResource.setDisability(this.getDisability());
        userResource.setEthnicity(this.getEthnicity().getId());
        userResource.setPassword(this.getPassword());
        userResource.setEmail(this.getEmail());
        userResource.setRoles(this.getRoles());

        return userResource;
    }
}
