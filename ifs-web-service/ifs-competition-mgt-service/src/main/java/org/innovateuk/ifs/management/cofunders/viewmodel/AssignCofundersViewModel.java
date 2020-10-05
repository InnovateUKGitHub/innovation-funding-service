package org.innovateuk.ifs.management.cofunders.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.Set;

public class AssignCofundersViewModel {

    private final long competitionId;
    private final String competitionName;
    private final long applicationId;
    private final String applicationName;
    private final String innovationArea;
    private final CofundersAvailableForApplicationPageResource cofundersAvailableForApplicationPage;
    private final String filter;

    public AssignCofundersViewModel(CompetitionResource competition, ApplicationResource application, String filter,
                                    CofundersAvailableForApplicationPageResource cofundersAvailableForApplicationPage) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.innovationArea = application.getInnovationArea().getSectorName();
        this.filter = filter;
        this.cofundersAvailableForApplicationPage = cofundersAvailableForApplicationPage;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public String getFilter() {
        return filter;
    }

    public CofundersAvailableForApplicationPageResource getCofundersAvailableForApplicationPage() {
        return cofundersAvailableForApplicationPage;
    }

}
