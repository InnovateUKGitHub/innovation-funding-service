package org.innovateuk.ifs.cofunder.resource;

import java.util.List;

public class CofunderDashboardCompetitionResource {

    private List<CofunderDashboardCompetitionAwaitingResource> cofunderDashboardCompetitionAwaitingResource;
    private List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource;

    public CofunderDashboardCompetitionResource() {
    }

    public CofunderDashboardCompetitionResource(List<CofunderDashboardCompetitionAwaitingResource> cofunderDashboardCompetitionAwaitingResource,
                                                List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource) {
        this.cofunderDashboardCompetitionAwaitingResource = cofunderDashboardCompetitionAwaitingResource;
        this.cofunderDashboardCompetitionUpcomingResource = cofunderDashboardCompetitionUpcomingResource;
    }

    public List<CofunderDashboardCompetitionAwaitingResource> getCofunderDashboardCompetitionAwaitingResource() {
        return cofunderDashboardCompetitionAwaitingResource;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getCofunderDashboardCompetitionUpcomingResource() {
        return cofunderDashboardCompetitionUpcomingResource;
    }
}