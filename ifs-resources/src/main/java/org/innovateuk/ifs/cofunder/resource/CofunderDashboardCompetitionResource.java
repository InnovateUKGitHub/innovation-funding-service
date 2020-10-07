package org.innovateuk.ifs.cofunder.resource;

import java.util.List;

public class CofunderDashboardCompetitionResource {

    private List<CofunderDashboardCompetitionActiveResource> activeCompetitions;
    private List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions;

    public CofunderDashboardCompetitionResource() {
    }

    public CofunderDashboardCompetitionResource(List<CofunderDashboardCompetitionActiveResource> activeCompetitions,
                                                List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions) {
        this.activeCompetitions = activeCompetitions;
        this.upcomingCompetitions = upcomingCompetitions;
    }

    public List<CofunderDashboardCompetitionActiveResource> getActiveCompetitions() {
        return activeCompetitions;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }
}