package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;

import java.util.stream.Collectors;

/**
 * View model for showing the Previous competitions
 */
public class PreviousDashboardViewModel extends DashboardViewModel {

    private CompetitionSearchResult pagination;

    public PreviousDashboardViewModel(CompetitionSearchResult searchResult, CompetitionCountResource counts, DashboardTabsViewModel tabs) {
        this.competitions = searchResult.getContent().stream().collect(Collectors.groupingBy((comp) -> CompetitionStatus.PREVIOUS));
        this.pagination = searchResult;
        this.counts = counts;
        this.tabs = tabs;
    }

    public CompetitionSearchResult getPagination() {
        return pagination;
    }
}
