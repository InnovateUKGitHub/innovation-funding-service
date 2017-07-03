package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * View model for manage funding applications page
 */
public class ManageFundingApplicationViewModel {

    private ApplicationSummaryPageResource results;
    private String sortField;
    private long competitionId;
    private String competitionName;
    private CompetitionInFlightStatsViewModel keyStatistics;
    private PaginationViewModel pagination;
    private boolean selectAllDisabled;


    public ManageFundingApplicationViewModel(ApplicationSummaryPageResource results, CompetitionInFlightStatsViewModel keyStatistics, PaginationViewModel pagination, String sortField, long competitionId, String competitionName, boolean selectAllDisabled) {
        this.results = results;
        this.sortField = sortField;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.keyStatistics = keyStatistics;
        this.pagination = pagination;
        this.selectAllDisabled = selectAllDisabled;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }

    public ApplicationSummaryPageResource getResults() {
        return results;
    }

    public String getSortField() {
        return sortField;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ApplicationSummaryResource> getContent() {
        return results != null ? results.getContent() : emptyList();
    }

    public boolean isAnythingChangeable() {
        if (results != null) {
            return results.getContent().stream().anyMatch(ApplicationSummaryResource::applicationFundingDecisionIsChangeable);
        } else {
            return false;
        }
    }

    public CompetitionInFlightStatsViewModel getKeyStatistics() {
        return keyStatistics;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public void setSelectAllDisabled(boolean selectAllDisabled) {
        this.selectAllDisabled = selectAllDisabled;
    }
}
