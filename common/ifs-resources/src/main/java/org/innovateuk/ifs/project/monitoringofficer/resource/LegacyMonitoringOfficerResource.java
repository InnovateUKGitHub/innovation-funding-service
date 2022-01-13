package org.innovateuk.ifs.project.monitoringofficer.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.ValidationConstants;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.innovateuk.ifs.commons.validation.PhoneNumberValidator.VALID_PHONE_NUMBER;

public class LegacyMonitoringOfficerResource {

    private Long id;

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

    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.standard.email.format}")
    @NotBlank(message = "{validation.standard.email.required}")
    @Size(max = 254, message = "{validation.standard.email.length.max}")
    private String email;

    @NotBlank(message = "{validation.standard.phonenumber.required}")
    @Pattern(regexp = VALID_PHONE_NUMBER,  message= "{validation.standard.phonenumber.format}")
    private String phoneNumber;

    private Long project;

    public LegacyMonitoringOfficerResource() {
    }

    public LegacyMonitoringOfficerResource(String firstName, String lastName, String email, String phoneNumber, Long project) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long projectId) {
        this.project = projectId;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonIgnore
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LegacyMonitoringOfficerResource that = (LegacyMonitoringOfficerResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(email, that.email)
                .append(phoneNumber, that.phoneNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(phoneNumber)
                .toHashCode();
    }

}
