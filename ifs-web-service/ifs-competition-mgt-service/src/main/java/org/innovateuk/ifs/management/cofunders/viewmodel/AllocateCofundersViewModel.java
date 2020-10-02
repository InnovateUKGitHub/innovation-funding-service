package org.innovateuk.ifs.management.cofunders.viewmodel;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class AllocateCofundersViewModel {
    private final Long competitionId;
    private final String competitionName;
    private final ApplicationsForCofundingPageResource applicationsPage;

    public AllocateCofundersViewModel(CompetitionResource competition, ApplicationsForCofundingPageResource applicationsPage) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
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
}
