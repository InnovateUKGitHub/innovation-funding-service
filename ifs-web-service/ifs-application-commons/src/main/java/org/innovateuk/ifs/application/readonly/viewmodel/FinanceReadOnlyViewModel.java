package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationProcurementMilestoneViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationResearchParticipationViewModel;

public class FinanceReadOnlyViewModel implements ApplicationQuestionReadOnlyViewModel, BaseAnalyticsViewModel {

    private final long applicationId;
    private final boolean fullyFunded;
    private final long financeSectionId;
    private final ApplicationProcurementMilestoneViewModel applicationProcurementMilestoneResources;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final ApplicationResearchParticipationViewModel applicationResearchParticipationViewModel;
    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;
    private final boolean collaborativeProject;
    private final boolean open;
    private final boolean ktpCompetition;
    private final boolean procurementMilestones;

    public FinanceReadOnlyViewModel(long applicationId, boolean fullyFunded, long financeSectionId,
                                    ApplicationProcurementMilestoneViewModel applicationProcurementMilestoneResources,
                                    ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel,
                                    ApplicationResearchParticipationViewModel applicationResearchParticipationViewModel,
                                    ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel,
                                    boolean collaborativeProject, boolean ktpCompetition, boolean procurementMilestones) {
        this.applicationId = applicationId;
        this.fullyFunded = fullyFunded;
        this.financeSectionId = financeSectionId;
        this.applicationProcurementMilestoneResources = applicationProcurementMilestoneResources;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.applicationResearchParticipationViewModel = applicationResearchParticipationViewModel;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
        this.collaborativeProject = collaborativeProject;
        this.open = !applicationFinanceSummaryViewModel.isReadOnly();
        this.ktpCompetition = ktpCompetition;
        this.procurementMilestones = procurementMilestones;
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
    
    public ApplicationProcurementMilestoneViewModel getApplicationProcurementMilestoneResources() {
        return applicationProcurementMilestoneResources;
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

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public boolean isProcurementMilestones() {
        return procurementMilestones;
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
        return getApplicationFinanceSummaryViewModel().getFinanceSummaryTableViewModel().isAllFinancesComplete() ;
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
