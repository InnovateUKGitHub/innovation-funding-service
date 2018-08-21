package org.innovateuk.ifs.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.user.resource.*;

public class AssessorProfileDetailsViewModel {

    private long assessorId;
    private Title title;
    private String name;
    private AddressResource address;
    private String phoneNumber;
    private String email;
    private BusinessType businessType;

    public AssessorProfileDetailsViewModel(UserResource user, ProfileResource profile) {
        this.assessorId = user.getId();
        this.title = user.getTitle();
        this.name = user.getName();
        this.address = profile.getAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.businessType = profile.getBusinessType();
    }

    public long getAssessorId() {
        return assessorId;
    }

    public Title getTitle() {
        return title;
    }

    public String getName() {
        return name;
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

    public BusinessType getBusinessType() {
        return businessType;
    }
}
