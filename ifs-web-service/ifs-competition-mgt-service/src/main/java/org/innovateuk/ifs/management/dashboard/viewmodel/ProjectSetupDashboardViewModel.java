package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

/**
 * View model for showing the competitions in project setup
 */
public class ProjectSetupDashboardViewModel extends DashboardViewModel {


    private Long countBankDetails;
    private boolean projectFinanceUser;

    public ProjectSetupDashboardViewModel(Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions,
                                          CompetitionCountResource counts,
                                          Long countBankDetails,
                                          DashboardTabsViewModel tabs,
                                          boolean projectFinanceUser) {
        this.competitions = competitions;
        this.counts = counts;
        this.tabs = tabs;
        this.countBankDetails = countBankDetails;
        this.projectFinanceUser = projectFinanceUser;
    }

    public Long getCountBankDetails() {
        return countBankDetails;
    }

    public boolean isProjectFinanceUser() {
        return projectFinanceUser;
    }
}
