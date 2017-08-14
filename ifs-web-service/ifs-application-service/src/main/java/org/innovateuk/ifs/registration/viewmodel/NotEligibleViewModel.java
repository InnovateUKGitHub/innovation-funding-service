package org.innovateuk.ifs.registration.viewmodel;

public class NotEligibleViewModel {
    private String organisationTypeName;

    public NotEligibleViewModel(String organisationTypeName) {
        this.organisationTypeName = organisationTypeName;
    }

    public String getOrganisationTypeName() {
        return organisationTypeName;
    }
}
