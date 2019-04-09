package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationResearchParticipationViewModel;

public class FinanceSummaryViewModel implements NewQuestionSummaryViewModel {

    private final long applicationId;
    private final boolean fullyFunded;
    private final long financeSectionId;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final ApplicationResearchParticipationViewModel applicationResearchParticipationViewModel;
    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;
    private final boolean collaborativeProject;


    public FinanceSummaryViewModel(long applicationId, boolean fullyFunded, long financeSectionId, ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel, ApplicationResearchParticipationViewModel applicationResearchParticipationViewModel, ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel, boolean collaborativeProject) {
        this.applicationId = applicationId;
        this.fullyFunded = fullyFunded;
        this.financeSectionId = financeSectionId;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.applicationResearchParticipationViewModel = applicationResearchParticipationViewModel;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
        this.collaborativeProject = collaborativeProject;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public boolean isFullyFunded() {
        return fullyFunded;
    }

    public long getFinanceSectionId() {
        return financeSectionId;
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    public ApplicationResearchParticipationViewModel getApplicationResearchParticipationViewModel() {
        return applicationResearchParticipationViewModel;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isOpen() {
        return true;
    }

    @Override
    public String getName() {
        return "Finance summary";
    }

    @Override
    public String getFragment() {
        return "finance-summary";
    }
}
