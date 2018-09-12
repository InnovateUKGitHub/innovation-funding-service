package org.innovateuk.ifs.application.common.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;

/**
 * View model for finance/finance-summary :: research_participation_alert.
 */
public class ApplicationResearchParticipationViewModel {

    private final double researchParticipationPercentage;
    private final CompetitionResource currentCompetition;

    public ApplicationResearchParticipationViewModel(double researchParticipationPercentage, CompetitionResource currentCompetition) {
        this.researchParticipationPercentage = researchParticipationPercentage;
        this.currentCompetition = currentCompetition;
    }

    public double getResearchParticipationPercentage() {
        return researchParticipationPercentage;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }
}
