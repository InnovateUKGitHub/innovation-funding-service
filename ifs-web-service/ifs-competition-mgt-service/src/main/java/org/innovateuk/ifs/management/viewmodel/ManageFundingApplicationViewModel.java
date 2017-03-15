package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;

import java.util.Collections;
import java.util.List;

public class ManageFundingApplicationViewModel {

    private ApplicationSummaryPageResource results;
    private String sortField;
    private String filter;
    private long competitionId;
    private String competitionName;
    private CompetitionInFlightStatsViewModel keyStatistics;
    private PaginationViewModel pagination;


    public ManageFundingApplicationViewModel(ApplicationSummaryPageResource results, CompetitionInFlightStatsViewModel keyStatistics, PaginationViewModel pagination, String sortField, String filter, long competitionId, String competitionName) {
        this.results = results;
        this.sortField = sortField;
        this.filter = filter;
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

    public String getFilter() {
        return filter;
    }

    public List<ApplicationSummaryResource> getContent(){
        return results != null ? results.getContent() : Collections.emptyList();
    }

    public CompetitionInFlightStatsViewModel getKeyStatistics() {
        return keyStatistics;
    }
}
