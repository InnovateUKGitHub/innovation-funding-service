package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

public class CompetitionSearchDashboardViewModel extends DashboardViewModel {

    private String searchQuery;
    private CompetitionSearchResult competitionSearchResult;

    public CompetitionSearchDashboardViewModel(
            CompetitionSearchResult competitionSearchResult,
            CompetitionCountResource counts,
            DashboardTabsViewModel tabs,
            String searchQuery) {
        this.counts = counts;
        this.tabs = tabs;
        this.competitionSearchResult = competitionSearchResult;
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public CompetitionSearchResult getCompetitionSearchResult() {
        return competitionSearchResult;
    }

    public void setCompetitionSearchResult(CompetitionSearchResult competitionSearchResult) {
        this.competitionSearchResult = competitionSearchResult;
    }
}
