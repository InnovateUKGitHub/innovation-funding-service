package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;

/**
 * View model for showing the non-IFS competitions
 */
public class NonIfsDashboardViewModel extends DashboardViewModel {

    private CompetitionSearchResult pagination;

    public NonIfsDashboardViewModel(CompetitionSearchResult searchResult, CompetitionCountResource counts, DashboardTabsViewModel tabs) {
        this.competitions = searchResult.getMappedCompetitions();
        this.pagination = searchResult;
        this.counts = counts;
        this.tabs = tabs;
    }

    public CompetitionSearchResult getPagination() {
        return pagination;
    }
}
