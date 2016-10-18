package com.worth.ifs.assessment.viewmodel.profile;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.Gender;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Assessor user details view.
 */
public class AssessorProfileDetailsViewModel {
    private String title;
    private String firstName;
    private String lastName;
    private Gender gender;
    private AddressResource address;
    private EthnicityResource ethnicity;
    private Disability disability;
    private String phoneNumber;
    private String email;

    public AssessorProfileDetailsViewModel(UserResource user, AddressResource address, EthnicityResource ethnicity) {
        this.title = user.getTitle();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.gender = user.getGender();
        this.address = address;
        this.ethnicity = ethnicity;
        this.disability = user.getDisability();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
    }

    public EthnicityResource getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(EthnicityResource ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Disability getDisability() {
        return disability;
    }

    public void setDisability(Disability disability) {
        this.disability = disability;
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
                .append(title, that.title)
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(gender, that.gender)
                .append(address, that.address)
                .append(ethnicity, that.ethnicity)
                .append(disability, that.disability)
                .append(phoneNumber, that.phoneNumber)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(title)
                .append(firstName)
                .append(lastName)
                .append(gender)
                .append(address)
                .append(ethnicity)
                .append(disability)
                .append(phoneNumber)
                .append(email)
                .toHashCode();
    }
}
