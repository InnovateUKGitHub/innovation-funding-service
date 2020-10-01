package org.innovateuk.ifs.management.cofunders.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class ManageCofundersViewModel {

    private final Long competitionId;
    private final String competitionName;

    public ManageCofundersViewModel(CompetitionResource competition) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }
}
