package org.innovateuk.ifs.organisation.viewmodel;

public class ConfirmResearchOrganisationEligibilityViewModel {

    private long competitionId;
    private long organisationId;
    private String organisationName;

    public ConfirmResearchOrganisationEligibilityViewModel(long competitionId, long organisationId, String organisationName) {
        this.competitionId = competitionId;
        this.organisationId = organisationId;
        this.organisationName = organisationName;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(long organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }
}
