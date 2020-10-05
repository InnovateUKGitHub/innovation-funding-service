package org.innovateuk.ifs.management.cofunders.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class AssignCofundersViewModel {

    private final long competitionId;
    private final String competitonName;
    private final long applicationId;
    private final String applicationName;
    private final CofundersAvailableForApplicationPageResource cofundersAvailableForApplicationPage;

    public AssignCofundersViewModel(CompetitionResource competition, ApplicationResource application,
                                    CofundersAvailableForApplicationPageResource cofundersAvailableForApplicationPage) {
        this.competitionId = competition.getId();
        this.competitonName = competition.getName();
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.cofundersAvailableForApplicationPage = cofundersAvailableForApplicationPage;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitonName() {
        return competitonName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public CofundersAvailableForApplicationPageResource getCofundersAvailableForApplicationPage() {
        return cofundersAvailableForApplicationPage;
    }
}
