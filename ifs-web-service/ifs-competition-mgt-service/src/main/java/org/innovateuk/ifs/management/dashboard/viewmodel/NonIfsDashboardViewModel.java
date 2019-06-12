package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;

public class NonIfsDashboardViewModel extends DashboardViewModel {

    private CompetitionSearchResult result;

    public NonIfsDashboardViewModel(CompetitionSearchResult result, CompetitionCountResource counts, DashboardTabsViewModel tabs) {
        this.result = result;
        this.counts = counts;
        this.tabs = tabs;
    }

    public CompetitionSearchResult getResult() {
        return result;
    }
}
