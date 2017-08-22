package org.innovateuk.ifs.application.creation.viewmodel;

public class AuthenticatedNotEligibleViewModel {

    private String organisationTypeName;
    private Long competitionId;

    public AuthenticatedNotEligibleViewModel(String organisationTypeName, Long competitionId) {
        this.organisationTypeName = organisationTypeName;
        this.competitionId = competitionId;
    }

    public String getOrganisationTypeName() {
        return organisationTypeName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }
}
