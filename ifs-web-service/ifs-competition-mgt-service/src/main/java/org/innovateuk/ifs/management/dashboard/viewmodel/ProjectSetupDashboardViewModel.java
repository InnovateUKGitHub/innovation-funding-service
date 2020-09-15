package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;

/**
 * View model for showing the competitions in project setup
 */
public class ProjectSetupDashboardViewModel extends DashboardViewModel {

    private CompetitionSearchResult result;
    private Long countBankDetails;
    private boolean projectFinanceUser;
    private boolean externalFinanceUser;

    public ProjectSetupDashboardViewModel(CompetitionSearchResult searchResult,
                                          CompetitionCountResource counts,
                                          Long countBankDetails,
                                          DashboardTabsViewModel tabs,
                                          boolean projectFinanceUser,
                                          boolean externalFinanceUser) {
        this.counts = counts;
        this.tabs = tabs;
        this.countBankDetails = countBankDetails;
        this.projectFinanceUser = projectFinanceUser;
        this.result = searchResult;
        this.externalFinanceUser = externalFinanceUser;

    }

    public Long getCountBankDetails() {
        return countBankDetails;
    }

    public boolean isProjectFinanceUser() {
        return projectFinanceUser;
    }

    public CompetitionSearchResult getResult() {
        return result;
    }

    public boolean isExternalFinanceUser() {
        return externalFinanceUser;
    }

    public List<CompetitionSearchResultItem> getNonPrioritisedCompetitions() {
        return competitions.entrySet().stream()
                .filter(entry -> !asList(PROJECT_SETUP, ASSESSOR_FEEDBACK, FUNDERS_PANEL).contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .collect(toList());
    }
}
