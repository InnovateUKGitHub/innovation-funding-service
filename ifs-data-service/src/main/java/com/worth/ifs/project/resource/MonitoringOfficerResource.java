package com.worth.ifs.project.resource;

import com.worth.ifs.commons.validation.ValidationConstants;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class MonitoringOfficerResource {

    private Long id;

    @NotEmpty(message = "Please enter a first name")
    private String firstName;

    @NotEmpty(message = "Please enter a last name")
    private String lastName;

    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "Please enter a valid email address")
    @NotEmpty(message = "Please enter your email")
    @Size(max = 256, message = "Your email address has a maximum length of 256 characters")
    private String email;

    @NotEmpty(message = "Please enter a phone number")
    @Size.List ({
            @Size(min=8, message="Input for your phone number has a minimum length of 8 characters"),
            @Size(max=20, message="Input for your phone number has a maximum length of 20 characters")
    })
    @Pattern(regexp = "([0-9\\ +-])+",  message= "Please enter a valid phone number")
    private String phoneNumber;

    private Long project;

    public MonitoringOfficerResource() {
    }

    public MonitoringOfficerResource(String firstName, String lastName, String email, String phoneNumber, Long project) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MonitoringOfficerResource that = (MonitoringOfficerResource) o;

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
