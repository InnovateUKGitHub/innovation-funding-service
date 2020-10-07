package org.innovateuk.ifs.cofunder.resource;

import java.util.List;

public class CofunderDashboardCompetitionResource {

    private List<CofunderDashboardCompetitionActiveResource> cofunderDashboardCompetitionActiveResource;
    private List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource;

    public CofunderDashboardCompetitionResource() {
    }

    public CofunderDashboardCompetitionResource(List<CofunderDashboardCompetitionActiveResource> cofunderDashboardCompetitionActiveResource,
                                                List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource) {
        this.cofunderDashboardCompetitionActiveResource = cofunderDashboardCompetitionActiveResource;
        this.cofunderDashboardCompetitionUpcomingResource = cofunderDashboardCompetitionUpcomingResource;
    }

    public List<CofunderDashboardCompetitionActiveResource> getCofunderDashboardCompetitionActiveResource() {
        return cofunderDashboardCompetitionActiveResource;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getCofunderDashboardCompetitionUpcomingResource() {
        return cofunderDashboardCompetitionUpcomingResource;
    }
}