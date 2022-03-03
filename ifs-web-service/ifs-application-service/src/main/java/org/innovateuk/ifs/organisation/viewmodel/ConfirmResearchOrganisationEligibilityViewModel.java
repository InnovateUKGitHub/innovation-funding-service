package org.innovateuk.ifs.organisation.viewmodel;

public class ConfirmResearchOrganisationEligibilityViewModel {

    private long competitionId;
    private long organisationId;
    private boolean isLeadJourney;

    public ConfirmResearchOrganisationEligibilityViewModel(long competitionId, long organisationId, boolean isLeadJourney) {
        this.competitionId = competitionId;
        this.organisationId = organisationId;
        this.isLeadJourney = isLeadJourney;
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

    public boolean isLeadJourney() {
        return isLeadJourney;
    }

    public void setLeadJourney(boolean leadJourney) {
        isLeadJourney = leadJourney;
    }
}
