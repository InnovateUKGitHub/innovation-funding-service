package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.controller.dashboard.DashboardTabsViewModel;

import java.util.List;
import java.util.Map;

/**
 * View model for showing the competitions in project setup
 */
public class ProjectSetupDashboardViewModel extends DashboardViewModel {


    private Long countBankDetails;

    public ProjectSetupDashboardViewModel(Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions,
                                          CompetitionCountResource counts,
                                          Long countBankDetails,
                                          DashboardTabsViewModel tabs) {
        this.competitions = competitions;
        this.counts = counts;
        this.tabs = tabs;
        this.countBankDetails = countBankDetails;
    }

    public Long getCountBankDetails() {
        return countBankDetails;
    }
}
