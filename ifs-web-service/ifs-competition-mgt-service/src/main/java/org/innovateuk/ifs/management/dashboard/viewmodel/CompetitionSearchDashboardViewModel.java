package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSupport;

/**
 * A view model for displaying the competition search results when searched on the dashboard by the internal user.
 */
public class CompetitionSearchDashboardViewModel {

    private final CompetitionSearchResult competitions;
    private String searchQuery;
    private UserResource user;

    public CompetitionSearchDashboardViewModel(CompetitionSearchResult competitions, String searchQuery, UserResource user) {
        this.competitions = competitions;
        this.searchQuery = searchQuery;
        this.user = user;
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
        return isInternal(user);
    }

    public boolean isSupportUser() {
        return isSupport(user);
    }
}
