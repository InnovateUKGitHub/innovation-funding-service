package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.viewmodel;

public class LeadInternationalOrganisationViewModel {

    private final long competitionId;
    private final Boolean leadInternationalOrganisationsApplicable;

    public LeadInternationalOrganisationViewModel(long competitionId, Boolean leadInternationalOrganisationsApplicable) {
        this.competitionId = competitionId;
        this.leadInternationalOrganisationsApplicable = leadInternationalOrganisationsApplicable;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public Boolean isLeadInternationalOrganisationsApplicable() {
        return leadInternationalOrganisationsApplicable;
    }
}
