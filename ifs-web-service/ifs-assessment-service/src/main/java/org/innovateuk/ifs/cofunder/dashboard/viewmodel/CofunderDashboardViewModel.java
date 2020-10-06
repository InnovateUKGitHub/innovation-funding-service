package org.innovateuk.ifs.cofunder.dashboard.viewmodel;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionAwaitingResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionUpcomingResource;

import java.util.List;

public class CofunderDashboardViewModel {

    private List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource;
    private List<CofunderDashboardCompetitionAwaitingResource> cofunderDashboardCompetitionAwaitingResource;

    public CofunderDashboardViewModel(List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource,
                                      List<CofunderDashboardCompetitionAwaitingResource> cofunderDashboardCompetitionAwaitingResource) {
        this.cofunderDashboardCompetitionUpcomingResource = cofunderDashboardCompetitionUpcomingResource;
        this.cofunderDashboardCompetitionAwaitingResource = cofunderDashboardCompetitionAwaitingResource;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getCofunderDashboardCompetitionUpcomingResource() {
        return cofunderDashboardCompetitionUpcomingResource;
    }

    public void setCofunderDashboardCompetitionUpcomingResource(List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource) {
        this.cofunderDashboardCompetitionUpcomingResource = cofunderDashboardCompetitionUpcomingResource;
    }

    public List<CofunderDashboardCompetitionAwaitingResource> getCofunderDashboardCompetitionAwaitingResource() {
        return cofunderDashboardCompetitionAwaitingResource;
    }

    public void setCofunderDashboardCompetitionAwaitingResource(List<CofunderDashboardCompetitionAwaitingResource> cofunderDashboardCompetitionAwaitingResource) {
        this.cofunderDashboardCompetitionAwaitingResource = cofunderDashboardCompetitionAwaitingResource;
    }
}
