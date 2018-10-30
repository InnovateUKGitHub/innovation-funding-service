package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;

public class CompetitionSearchDashboardViewModel {

    private CompetitionSearchResult competitions;
    private String searchQuery;
    private boolean isInternalUser;

    public CompetitionSearchDashboardViewModel(CompetitionSearchResult competitions, String searchQuery, boolean isInternalUser) {
        this.competitions = competitions;
        this.searchQuery = searchQuery;
        this.isInternalUser = isInternalUser;
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

    public boolean isInternalUser() {
        return isInternalUser;
    }

    public void setInternalUser(boolean internalUser) {
        isInternalUser = internalUser;
    }
}
