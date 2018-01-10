package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.controller.dashboard.DashboardTabsViewModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<CompetitionSearchResultItem> getAllCompetitions(){
        return competitions.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
}
