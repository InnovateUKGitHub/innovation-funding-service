package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class LeadInternationalOrganisationViewModel {

    private final CompetitionResource competition;
    private final Boolean leadInternationalOrganisationsApplicable;

    public LeadInternationalOrganisationViewModel(CompetitionResource competition, Boolean leadInternationalOrganisationsApplicable) {
        this.competition = competition;
        this.leadInternationalOrganisationsApplicable = leadInternationalOrganisationsApplicable;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public Boolean isLeadInternationalOrganisationsApplicable() {
        return leadInternationalOrganisationsApplicable;
    }
}
