package org.innovateuk.ifs.cofunder.dashboard.viewmodel;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionActiveResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionUpcomingResource;

import java.util.List;

/**
 * Holder of model attributes for the Cofunder Dashboard.
 */
public class CofunderDashboardViewModel {

    private List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions;
    private List<CofunderDashboardCompetitionActiveResource> awaitingCompetitions;

    public CofunderDashboardViewModel(List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource,
                                      List<CofunderDashboardCompetitionActiveResource> awaitingCompetitions) {
        this.upcomingCompetitions = cofunderDashboardCompetitionUpcomingResource;
        this.awaitingCompetitions = awaitingCompetitions;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public void setUpcomingCompetitions(List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions) {
        this.upcomingCompetitions = upcomingCompetitions;
    }

    public List<CofunderDashboardCompetitionActiveResource> getAwaitingCompetitions() {
        return awaitingCompetitions;
    }

    public void setAwaitingCompetitions(List<CofunderDashboardCompetitionActiveResource> awaitingCompetitions) {
        this.awaitingCompetitions = awaitingCompetitions;
    }
}
