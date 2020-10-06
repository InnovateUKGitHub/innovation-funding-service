package org.innovateuk.ifs.cofunder.dashboard.viewmodel;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionAwaitingResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionUpcomingResource;

import java.util.List;

/**
 * Holder of model attributes for the Cofunder Dashboard.
 */
public class CofunderDashboardViewModel {

    private List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions;
    private List<CofunderDashboardCompetitionAwaitingResource> awaitingCompetitions;

    public CofunderDashboardViewModel(List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource,
                                      List<CofunderDashboardCompetitionAwaitingResource> awaitingCompetitions) {
        this.upcomingCompetitions = cofunderDashboardCompetitionUpcomingResource;
        this.awaitingCompetitions = awaitingCompetitions;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public void setUpcomingCompetitions(List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions) {
        this.upcomingCompetitions = upcomingCompetitions;
    }

    public List<CofunderDashboardCompetitionAwaitingResource> getAwaitingCompetitions() {
        return awaitingCompetitions;
    }

    public void setAwaitingCompetitions(List<CofunderDashboardCompetitionAwaitingResource> awaitingCompetitions) {
        this.awaitingCompetitions = awaitingCompetitions;
    }
}
