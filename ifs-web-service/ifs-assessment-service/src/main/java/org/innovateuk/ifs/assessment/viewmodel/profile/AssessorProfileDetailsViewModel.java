package org.innovateuk.ifs.assessment.viewmodel.profile;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.user.resource.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Assessor details view.
 */
public class AssessorProfileDetailsViewModel {
    private String firstName;
    private String lastName;
    private AddressResource address;
    private String phoneNumber;
    private String email;

    public AssessorProfileDetailsViewModel(UserProfileResource profileDetails) {
        this.firstName = profileDetails.getFirstName();
        this.lastName = profileDetails.getLastName();
        this.address = profileDetails.getAddress();
        this.phoneNumber = profileDetails.getPhoneNumber();
        this.email = profileDetails.getEmail();
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

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

        AssessorProfileDetailsViewModel that = (AssessorProfileDetailsViewModel) o;

        return new EqualsBuilder()
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(address, that.address)
                .append(phoneNumber, that.phoneNumber)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(address)
                .append(phoneNumber)
                .append(email)
                .toHashCode();
    }
}
