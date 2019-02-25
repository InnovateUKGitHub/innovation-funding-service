package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;

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

    public List<CompetitionSearchResultItem> getNonPrioritisedCompetitions() {
        return competitions.entrySet().stream()
                .filter(entry -> !asList(PROJECT_SETUP, ASSESSOR_FEEDBACK, FUNDERS_PANEL).contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .collect(toList());
    }
}
