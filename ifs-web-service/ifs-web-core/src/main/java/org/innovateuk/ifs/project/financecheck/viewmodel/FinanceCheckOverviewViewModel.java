package org.innovateuk.ifs.project.financecheck.viewmodel;

/**
 * View model for the finance checks overview page.
 */
public class FinanceCheckOverviewViewModel {
    private ProjectFinanceOverviewViewModel overview;
    private FinanceCheckSummariesViewModel summaries;

    public FinanceCheckOverviewViewModel() {}

    public FinanceCheckOverviewViewModel(ProjectFinanceOverviewViewModel projectFinanceOverviewViewModel, FinanceCheckSummariesViewModel financeCheckSummaries) {
        this.overview = projectFinanceOverviewViewModel;
        this.summaries = financeCheckSummaries;
    }

    public ProjectFinanceOverviewViewModel getOverview() {
        return overview;
    }

    public void setOverview(ProjectFinanceOverviewViewModel overview) {
        this.overview = overview;
    }

    public FinanceCheckSummariesViewModel getSummaries() {
        return summaries;
    }

    public void setSummaries(FinanceCheckSummariesViewModel summaries) {
        this.summaries = summaries;
    }
}
