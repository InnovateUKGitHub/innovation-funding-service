package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.controller.dashboard.DashboardTabsViewModel;

import java.util.List;
import java.util.Map;

/**
 * View model for showing the Previous competitions
 */
public class PreviousDashboardViewModel extends DashboardViewModel {

    public PreviousDashboardViewModel(Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions, CompetitionCountResource counts, DashboardTabsViewModel tabs) {
        this.competitions = competitions;
        this.counts = counts;
        this.tabs = tabs;
    }
}
