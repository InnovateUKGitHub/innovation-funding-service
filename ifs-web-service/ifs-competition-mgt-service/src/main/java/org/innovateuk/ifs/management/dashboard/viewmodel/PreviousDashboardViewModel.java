package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;

/**
 * View model for showing the Previous competitions
 */
public class PreviousDashboardViewModel extends DashboardViewModel {

    private CompetitionSearchResult result;

    public PreviousDashboardViewModel(CompetitionSearchResult result, CompetitionCountResource counts, DashboardTabsViewModel tabs) {
        this.result = result;
        this.counts = counts;
        this.tabs = tabs;
    }

    public CompetitionSearchResult getResult() {
        return result;
    }
}
