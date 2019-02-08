package org.innovateuk.ifs.registration.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.UserResource;


/**
 * DTO for registering a Monitoring officer user
 */
public class MonitoringOfficerRegistrationResource {

    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String password;

    public MonitoringOfficerRegistrationResource() {
        firstName = null;
        lastName = null;
        phoneNumber = null;
        password = null;
    }

    public MonitoringOfficerRegistrationResource(String firstName, String lastName, String phoneNumber, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public UserResource toUserResource() {
        final UserResource userResource = new UserResource();
        userResource.setFirstName(this.getFirstName());
        userResource.setLastName(this.getLastName());
        userResource.setPhoneNumber(this.getPhoneNumber());
        userResource.setPassword(this.getPassword());
        return userResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MonitoringOfficerRegistrationResource that = (MonitoringOfficerRegistrationResource) o;

        return new EqualsBuilder()
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(phoneNumber, that.phoneNumber)
                .append(password, that.password)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(phoneNumber)
                .append(password)
                .toHashCode();
    }
}