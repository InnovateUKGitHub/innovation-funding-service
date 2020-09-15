package org.innovateuk.ifs.organisation.viewmodel;

import java.util.Set;

public class OrganisationSelectionViewModel {

    private final Set<OrganisationSelectionChoiceViewModel> choices;
    private final boolean collaboratorJourney;
    private final boolean applicantJourney;
    private final boolean internationalJourney;
    private final String newOrganisationUrl;

    public OrganisationSelectionViewModel(Set<OrganisationSelectionChoiceViewModel> choices, boolean collaboratorJourney, boolean applicantJourney, boolean internationalJourney, String newOrganisationUrl) {
        this.choices = choices;
        this.collaboratorJourney = collaboratorJourney;
        this.newOrganisationUrl = newOrganisationUrl;
        this.applicantJourney = applicantJourney;
        this.internationalJourney = internationalJourney;
    }

    public Set<OrganisationSelectionChoiceViewModel> getChoices() {
        return choices;
    }

    public boolean isCollaboratorJourney() {
        return collaboratorJourney;
    }

    public boolean isApplicantJourney() {
        return applicantJourney;
    }

    public boolean isInternationalJourney() {
        return internationalJourney;
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
