package org.innovateuk.ifs.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.user.resource.*;

public class AssessorProfileDetailsViewModel {

    private Title title;
    private String firstName;
    private String lastName;
    private Gender gender;
    private AddressResource address;
    private String phoneNumber;
    private String email;

    public AssessorProfileDetailsViewModel(UserResource user, AddressResource addressResource) {
        this.title = user.getTitle();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.gender = user.getGender();
        this.address = addressResource;
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
    }

    public Title getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public AddressResource getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}
