package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationResearchParticipationViewModel;

public class FinanceReadOnlyViewModel implements ApplicationQuestionReadOnlyViewModel, BaseAnalyticsViewModel {

    private final long applicationId;
    private final boolean fullyFunded;
    private final long financeSectionId;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final ApplicationResearchParticipationViewModel applicationResearchParticipationViewModel;
    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;
    private final boolean collaborativeProject;
    private final boolean open;

    public FinanceReadOnlyViewModel(long applicationId, boolean fullyFunded, long financeSectionId, ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel, ApplicationResearchParticipationViewModel applicationResearchParticipationViewModel, ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel, boolean collaborativeProject) {
        this.applicationId = applicationId;
        this.fullyFunded = fullyFunded;
        this.financeSectionId = financeSectionId;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.applicationResearchParticipationViewModel = applicationResearchParticipationViewModel;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
        this.collaborativeProject = collaborativeProject;
        this.open = !applicationFinanceSummaryViewModel.isReadOnly();
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return applicationFinanceSummaryViewModel.getCompetitionName();
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
        return open;
    }

    @Override
    public String getName() {
        return fullyFunded ? "Funding breakdown" : "Finances summary";
    }

    @Override
    public String getFragment() {
        return "finance-summary";
    }

    @Override
    public boolean isComplete() {
        return getApplicationFinanceSummaryViewModel().isAllFinancesComplete() ;
    }

    @Override
    public boolean shouldDisplayActions() {
        return false;
    }

    @Override
    public boolean shouldDisplayMarkAsComplete() {
        return false;
    }

    @Override
    public boolean isLead() {
        return false; // not required
    }

}
