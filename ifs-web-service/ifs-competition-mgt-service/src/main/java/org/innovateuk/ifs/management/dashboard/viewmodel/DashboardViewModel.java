package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;

import java.util.List;
import java.util.Map;

/**
 * Abstract view model for sharing attributes that are on all dashboards
 */
public abstract class DashboardViewModel {
    protected Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions;
    protected CompetitionCountResource counts;
    protected DashboardTabsViewModel tabs;

    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getCompetitions() {
        return competitions;
    }

    public CompetitionCountResource getCounts() {
        return counts;
    }

    public DashboardTabsViewModel getTabs() {
        return tabs;
    }

    public boolean isSupportUser() {
        return tabs.support();
    }
}
