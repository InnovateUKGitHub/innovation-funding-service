package org.innovateuk.ifs.registration.viewmodel;

import java.util.Set;

public class OrganisationSelectionViewModel {

    private final Set<OrganisationSelectionChoiceViewModel> choices;
    private final boolean collaboratorJourney;
    private final String newOrganisationUrl;

    public OrganisationSelectionViewModel(Set<OrganisationSelectionChoiceViewModel> choices, boolean collaboratorJourney, String newOrganisationUrl) {
        this.choices = choices;
        this.collaboratorJourney = collaboratorJourney;
        this.newOrganisationUrl = newOrganisationUrl;
    }

    public Set<OrganisationSelectionChoiceViewModel> getChoices() {
        return choices;
    }

    public boolean isCollaboratorJourney() {
        return collaboratorJourney;
    }

    public String getNewOrganisationUrl() {
        return newOrganisationUrl;
    }

    /* view logic. */
    public boolean canSelectOrganisation() {
        return choices.size() > 1;
    }

    public OrganisationSelectionChoiceViewModel onlyOrganisation() {
        return choices.iterator().next();
    }
}
