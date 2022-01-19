package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

/**
 * View model for finance/finance-summary :: research_participation_alert.
 */
public class ApplicationResearchParticipationViewModel implements BaseAnalyticsViewModel {

    private final double researchParticipationPercentage;
    private final CompetitionResource currentCompetition;
    private final long applicationId;

    public ApplicationResearchParticipationViewModel(double researchParticipationPercentage, CompetitionResource currentCompetition, long applicationId) {
        this.researchParticipationPercentage = researchParticipationPercentage;
        this.currentCompetition = currentCompetition;
        this.applicationId = applicationId;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return currentCompetition.getName();
    }

    public double getResearchParticipationPercentage() {
        return researchParticipationPercentage;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }
}
