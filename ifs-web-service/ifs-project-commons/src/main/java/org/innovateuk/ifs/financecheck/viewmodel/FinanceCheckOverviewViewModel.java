package org.innovateuk.ifs.financecheck.viewmodel;

/**
 * View model for the finance checks overview page.
 */
public class FinanceCheckOverviewViewModel {
    private final ProjectFinanceOverviewViewModel overview;
    private final FinanceCheckSummariesViewModel summaries;
    private final ProjectFinanceCostBreakdownViewModel breakdown;

    private final long applicationId;
    private final boolean canChangeFunding;

    public FinanceCheckOverviewViewModel(ProjectFinanceOverviewViewModel overview, FinanceCheckSummariesViewModel summaries, ProjectFinanceCostBreakdownViewModel breakdown, long applicationId, boolean canChangeFunding) {
        this.overview = overview;
        this.summaries = summaries;
        this.breakdown = breakdown;
        this.applicationId = applicationId;
        this.canChangeFunding = canChangeFunding;
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

    public boolean isCanChangeFunding() {
        return canChangeFunding;
    }
}
