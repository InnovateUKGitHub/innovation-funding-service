package org.innovateuk.ifs.cofunder.resource;

import java.util.List;

public class CofunderDashboardCompetitionResource {

    private List<CofunderDashboardCompetitionActiveResource> activeCompetitions;
    private List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions;
    private List<CofunderDashboardCompetitionPreviousResource> previousCompetitions;

    public CofunderDashboardCompetitionResource() {
    }

    public CofunderDashboardCompetitionResource(List<CofunderDashboardCompetitionActiveResource> activeCompetitions,
                                                List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions,
                                                List<CofunderDashboardCompetitionPreviousResource> previousCompetitions) {
        this.activeCompetitions = activeCompetitions;
        this.upcomingCompetitions = upcomingCompetitions;
        this.previousCompetitions = previousCompetitions;
    }

    public List<CofunderDashboardCompetitionActiveResource> getActiveCompetitions() {
        return activeCompetitions;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public List<CofunderDashboardCompetitionPreviousResource> getPreviousCompetitions() {
        return previousCompetitions;
    }
}