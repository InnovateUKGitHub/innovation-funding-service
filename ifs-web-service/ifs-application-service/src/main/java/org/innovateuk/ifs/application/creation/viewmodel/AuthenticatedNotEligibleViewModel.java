package org.innovateuk.ifs.application.creation.viewmodel;

public class AuthenticatedNotEligibleViewModel {

    private String organisationTypeName;

    public AuthenticatedNotEligibleViewModel(String organisationTypeName) {
        this.organisationTypeName = organisationTypeName;
    }

    public String getOrganisationTypeName() {
        return organisationTypeName;
    }
}
