package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;

import java.util.Collections;
import java.util.List;

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


    public ManageFundingApplicationViewModel(ApplicationSummaryPageResource results, CompetitionInFlightStatsViewModel keyStatistics, PaginationViewModel pagination, String sortField, long competitionId, String competitionName) {
        this.results = results;
        this.sortField = sortField;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.keyStatistics = keyStatistics;
        this.pagination = pagination;
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

    public List<ApplicationSummaryResource> getContent(){
        return results != null ? results.getContent() : Collections.emptyList();
    }

    public CompetitionInFlightStatsViewModel getKeyStatistics() {
        return keyStatistics;
    }
}
