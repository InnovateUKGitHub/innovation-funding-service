package com.worth.ifs.registration.resource;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.user.resource.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * DTO for registering a User.
 */
public class UserRegistrationResource {

    @NotEmpty(message = "{validation.standard.title.selectionrequired}")
    @Size(max = 5, message = "{validation.standard.title.length.max}")
    @Pattern(regexp = "^(Mr|Miss|Mrs|Ms|Dr)$", message = "{validation.standard.title.format}")
    private String title;

    @NotEmpty(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} -]*", message = "{validation.standard.firstname.required}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.firstname.length.min}"),
            @Size(max = 70, message = "{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotEmpty(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} -]*", message = "{validation.standard.lastname.required}")
    @Size.List({
            @Size(min = 2, message = "{validation.standard.lastname.length.min}"),
            @Size(max = 70, message = "{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @NotEmpty(message = "{validation.standard.phonenumber.required}")
    @Size.List({
            @Size(min = 8, message = "{validation.standard.phonenumber.length.min}"),
            @Size(max = 20, message = "{validation.standard.phonenumber.length.max}")
    })
    @Pattern(regexp = "([0-9\\ +-])+", message = "{validation.standard.phonenumber.format}")
    private String phoneNumber;

    @NotNull(message = "validation.standard.gender.selectionrequired")
    private Gender gender;

    @NotNull(message = "validation.standard.disability.selectionrequired")
    private Disability disability;

    @NotNull(message = "validation.standard.ethnicity.selectionrequired")
    private EthnicityResource ethnicity;

    @NotEmpty(message = "{validation.standard.password.required}")
    @Size.List({
            @Size(min = 10, message = "{validation.standard.password.length.min}"),
            @Size(max = 30, message = "{validation.standard.password.length.max}"),
    })
    private String password;

    @NotNull(message = "{validation.standard.address.required}")
    @Valid
    private AddressResource address;

    private String email;

    private List<RoleResource> roles;

    public UserRegistrationResource() {
    }

    public UserRegistrationResource(String title, String firstName, String lastName, String phoneNumber, Gender gender, Disability disability, EthnicityResource ethnicity, String password, AddressResource address) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.disability = disability;
        this.ethnicity = ethnicity;
        this.password = password;
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Disability getDisability() {
        return disability;
    }

    public void setDisability(Disability disability) {
        this.disability = disability;
    }

    public EthnicityResource getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(EthnicityResource ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
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

        UserRegistrationResource that = (UserRegistrationResource) o;

        return new EqualsBuilder()
                .append(title, that.title)
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(phoneNumber, that.phoneNumber)
                .append(gender, that.gender)
                .append(disability, that.disability)
                .append(ethnicity, that.ethnicity)
                .append(password, that.password)
                .append(address, that.address)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(title)
                .append(firstName)
                .append(lastName)
                .append(phoneNumber)
                .append(gender)
                .append(disability)
                .append(ethnicity)
                .append(password)
                .append(address)
                .append(email)
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

    public List<RoleResource> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleResource> roles) {
        this.roles = roles;
    }
}
