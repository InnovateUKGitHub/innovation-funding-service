package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;

public class SearchBarViewModel {

    private CompetitionSearchResult competitions;
    private String searchQuery;
    private DashboardTabsViewModel tabs;

    public SearchBarViewModel(CompetitionSearchResult competitions, String searchQuery, DashboardTabsViewModel tabs) {
        this.competitions = competitions;
        this.searchQuery = searchQuery;
        this.tabs = tabs;
    }

    public CompetitionSearchResult getCompetitions() {
        return competitions;
    }

    public void setCompetitions(CompetitionSearchResult competitions) {
        this.competitions = competitions;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public DashboardTabsViewModel getTabs() {
        return tabs;
    }

    public void setTabs(DashboardTabsViewModel tabs) {
        this.tabs = tabs;
    }
}
