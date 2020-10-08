package org.innovateuk.ifs.cofunder.resource;

import java.util.List;

public class CofunderDashboardCompetitionResource {

    private List<CofunderDashboardCompetitionPendingResource> activeCompetitions;
    private List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions;
    private List<CofunderDashboardCompetitionPreviousResource> previousCompetitions;

    public CofunderDashboardCompetitionResource() {
    }

    public CofunderDashboardCompetitionResource(List<CofunderDashboardCompetitionPendingResource> activeCompetitions,
                                                List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions,
                                                List<CofunderDashboardCompetitionPreviousResource> previousCompetitions) {
        this.activeCompetitions = activeCompetitions;
        this.upcomingCompetitions = upcomingCompetitions;
        this.previousCompetitions = previousCompetitions;
    }

    public List<CofunderDashboardCompetitionPendingResource> getActiveCompetitions() {
        return activeCompetitions;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public List<CofunderDashboardCompetitionPreviousResource> getPreviousCompetitions() {
        return previousCompetitions;
    }
}