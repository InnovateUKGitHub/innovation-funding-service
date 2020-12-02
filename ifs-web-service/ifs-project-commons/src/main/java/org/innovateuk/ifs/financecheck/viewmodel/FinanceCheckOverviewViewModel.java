package org.innovateuk.ifs.financecheck.viewmodel;

import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;

/**
 * View model for the finance checks overview page.
 */
public class FinanceCheckOverviewViewModel {
    private final ProjectFinanceOverviewViewModel overview;
    private final FinanceCheckSummariesViewModel summaries;
    private final ProjectFinanceCostBreakdownViewModel breakdown;
    private final boolean loanCompetition;
    private final boolean ktpCompetition;
    private final boolean hasGrantClaimPercentage;

    private final long applicationId;
    private final boolean canChangeFundingSought;
    private final boolean canChangeFundingLevelPercentages;

    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;

    public FinanceCheckOverviewViewModel(ProjectFinanceOverviewViewModel overview, FinanceCheckSummariesViewModel summaries, ProjectFinanceCostBreakdownViewModel breakdown, long applicationId,
                                         boolean canChangeFundingSought, boolean loanCompetition, boolean ktpCompetition, boolean canChangeFundingLevelPercentages, boolean hasGrantClaimPercentage, ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel) {
        this.overview = overview;
        this.summaries = summaries;
        this.breakdown = breakdown;
        this.applicationId = applicationId;
        this.canChangeFundingSought = canChangeFundingSought;
        this.loanCompetition = loanCompetition;
        this.ktpCompetition = ktpCompetition;
        this.canChangeFundingLevelPercentages = canChangeFundingLevelPercentages;
        this.hasGrantClaimPercentage = hasGrantClaimPercentage;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
    }

    public ProjectFinanceOverviewViewModel getOverview() {
        return overview;
    }

    public FinanceCheckSummariesViewModel getSummaries() {
        return summaries;
    }

    public ProjectFinanceCostBreakdownViewModel getBreakdown() {
        return breakdown;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public boolean isHasGrantClaimPercentage() {
        return hasGrantClaimPercentage;
    }

    public boolean isCanChangeFundingSought() {
        return canChangeFundingSought;
    }

    public boolean isLoanCompetition() {
        return loanCompetition;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public boolean isCanChangeFundingLevelPercentages() {
        return canChangeFundingLevelPercentages;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }
}