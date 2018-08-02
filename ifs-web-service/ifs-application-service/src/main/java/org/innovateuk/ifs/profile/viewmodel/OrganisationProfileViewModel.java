package org.innovateuk.ifs.profile.viewmodel;

public class OrganisationProfileViewModel {

    private final String name;
    private final String registrationNumber;
    private final String type;

    public OrganisationProfileViewModel(String name, String registrationNumber, String type) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getType() {
        return type;
    }
}
