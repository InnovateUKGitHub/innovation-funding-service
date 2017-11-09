package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.controller.dashboard.DashboardTabsViewModel;

import java.util.List;
import java.util.Map;

/**
 * View model for showing the Upcoming competitions
 */
public class UpcomingDashboardViewModel extends DashboardViewModel {
    private List<String> formattedInnovationAreas;

    public UpcomingDashboardViewModel(Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions,
                                      CompetitionCountResource counts,
                                      List<String> formattedInnovationAreas,
                                      DashboardTabsViewModel tabs) {
        this.competitions = competitions;
        this.counts = counts;
        this.formattedInnovationAreas = formattedInnovationAreas;
        this.tabs = tabs;
    }

    public List<String> getFormattedInnovationAreas() {
        return formattedInnovationAreas;
    }
}
