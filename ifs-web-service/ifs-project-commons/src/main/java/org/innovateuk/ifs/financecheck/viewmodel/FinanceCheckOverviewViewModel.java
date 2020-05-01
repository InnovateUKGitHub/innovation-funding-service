package org.innovateuk.ifs.financecheck.viewmodel;

/**
 * View model for the finance checks overview page.
 */
public class FinanceCheckOverviewViewModel {
    private final ProjectFinanceOverviewViewModel overview;
    private final FinanceCheckSummariesViewModel summaries;
    private final ProjectFinanceCostBreakdownViewModel breakdown;
    private final boolean loanCompetition;
    private final boolean hasGrantClaimPercentage;

    private final long applicationId;
    private final boolean canChangeFundingSought;
    private final boolean canChangeFundingLevelPercentages;

    public FinanceCheckOverviewViewModel(ProjectFinanceOverviewViewModel overview, FinanceCheckSummariesViewModel summaries, ProjectFinanceCostBreakdownViewModel breakdown, long applicationId,
                                         boolean canChangeFundingSought, boolean loanCompetition, boolean canChangeFundingLevelPercentages, boolean hasGrantClaimPercentage) {
        this.overview = overview;
        this.summaries = summaries;
        this.breakdown = breakdown;
        this.applicationId = applicationId;
        this.canChangeFundingSought = canChangeFundingSought;
        this.loanCompetition = loanCompetition;
        this.canChangeFundingLevelPercentages = canChangeFundingLevelPercentages;
        this.hasGrantClaimPercentage = hasGrantClaimPercentage;
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

    public boolean isCanChangeFundingLevelPercentages() {
        return canChangeFundingLevelPercentages;
    }
}