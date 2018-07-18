package org.innovateuk.ifs.registration.model;

import java.util.Set;

public class OrganisationSelectionViewModel {

    private final Set<OrganisationSelectionChoiceViewModel> choices;
    private final boolean newApplication;
    private final String newOrganisationUrl;

    public OrganisationSelectionViewModel(Set<OrganisationSelectionChoiceViewModel> choices, boolean newApplication, String newOrganisationUrl) {
        this.choices = choices;
        this.newApplication = newApplication;
        this.newOrganisationUrl = newOrganisationUrl;
    }

    public Set<OrganisationSelectionChoiceViewModel> getChoices() {
        return choices;
    }

    public boolean isNewApplication() {
        return newApplication;
    }

    public String getNewOrganisationUrl() {
        return newOrganisationUrl;
    }
}
