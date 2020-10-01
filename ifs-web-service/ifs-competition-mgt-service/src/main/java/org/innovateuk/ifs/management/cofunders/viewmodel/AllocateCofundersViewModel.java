package org.innovateuk.ifs.management.cofunders.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

public class AllocateCofundersViewModel {
    private final Long competitionId;
    private final String competitionName;
    private final List<ApplicationResource> applications;

    public AllocateCofundersViewModel(CompetitionResource competition, List<ApplicationResource> applications) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.applications = applications;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ApplicationResource> getApplications() {
        return applications;
    }
}
