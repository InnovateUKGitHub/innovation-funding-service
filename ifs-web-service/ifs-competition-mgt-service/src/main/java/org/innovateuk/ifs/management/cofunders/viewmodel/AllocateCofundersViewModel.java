package org.innovateuk.ifs.management.cofunders.viewmodel;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.pagination.PaginationViewModel;

public class AllocateCofundersViewModel {
    private final Long competitionId;
    private final String competitionName;
    private final String filter;
    private final ApplicationsForCofundingPageResource applicationsPage;


    public AllocateCofundersViewModel(CompetitionResource competition, String filter, ApplicationsForCofundingPageResource applicationsPage) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.filter = filter;
        this.applicationsPage = applicationsPage;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public ApplicationsForCofundingPageResource getApplicationsPage() {
        return applicationsPage;
    }

    public String getFilter() {
        return filter;
    }

    public PaginationViewModel getPagination() {
        return new PaginationViewModel(applicationsPage);
    }

}
