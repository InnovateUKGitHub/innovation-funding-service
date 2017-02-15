package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class ManageFundingApplicationViewModel {

    private ApplicationSummaryPageResource results;
    private String sortField;
    private String filter;
    private CompetitionResource competitionResource;

    public CompetitionResource getCompetitionResource() {
        return competitionResource;
    }

    public ManageFundingApplicationViewModel(ApplicationSummaryPageResource results, String sortField, String filter, CompetitionResource competitionResource) {
        this.results = results;
        this.sortField = sortField;
        this.filter = filter;
        this.competitionResource = competitionResource;
    }

    public ApplicationSummaryPageResource getResults() {
        return results;
    }

    public String getSortField() {
        return sortField;
    }

    public String getFilter() {
        return filter;
    }
}
