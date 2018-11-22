package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;

/**
 * A view model for displaying the competition search results when searched on the dashboard by the internal user.
 */
public class CompetitionSearchDashboardViewModel {

    private final CompetitionSearchResult competitions;
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
