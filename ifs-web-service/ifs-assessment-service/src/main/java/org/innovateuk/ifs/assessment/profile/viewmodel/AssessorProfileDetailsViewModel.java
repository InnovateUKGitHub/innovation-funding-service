package org.innovateuk.ifs.assessment.profile.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.user.resource.*;

public class AssessorProfileDetailsViewModel {

    private Title title;
    private String firstName;
    private String lastName;
    private Gender gender;
    private AddressResource address;
    private EthnicityResource ethnicity;
    private Disability disability;
    private String phoneNumber;
    private String email;

    public AssessorProfileDetailsViewModel(UserProfileResource profileDetails) {
        this.title = profileDetails.getTitle();
        this.firstName = profileDetails.getFirstName();
        this.lastName = profileDetails.getLastName();
        this.gender = profileDetails.getGender();
        this.address = profileDetails.getAddress();
        this.ethnicity = profileDetails.getEthnicity();
        this.disability = profileDetails.getDisability();
        this.phoneNumber = profileDetails.getPhoneNumber();
        this.email = profileDetails.getEmail();
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

    public EthnicityResource getEthnicity() {
        return ethnicity;
    }

    public Disability getDisability() {
        return disability;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}
