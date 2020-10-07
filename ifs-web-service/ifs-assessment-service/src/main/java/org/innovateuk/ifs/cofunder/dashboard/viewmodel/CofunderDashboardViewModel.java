package org.innovateuk.ifs.cofunder.dashboard.viewmodel;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionActiveResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionUpcomingResource;

import java.util.List;

/**
 * Holder of model attributes for the Cofunder Dashboard.
 */
public class CofunderDashboardViewModel {

    private List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions;
    private List<CofunderDashboardCompetitionActiveResource> activeCompetitions;

    public CofunderDashboardViewModel(List<CofunderDashboardCompetitionUpcomingResource> cofunderDashboardCompetitionUpcomingResource,
                                      List<CofunderDashboardCompetitionActiveResource> activeCompetitions) {
        this.upcomingCompetitions = cofunderDashboardCompetitionUpcomingResource;
        this.activeCompetitions = activeCompetitions;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public void setUpcomingCompetitions(List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions) {
        this.upcomingCompetitions = upcomingCompetitions;
    }

    public List<CofunderDashboardCompetitionActiveResource> getActiveCompetitions() {
        return activeCompetitions;
    }

    public void setActiveCompetitions(List<CofunderDashboardCompetitionActiveResource> activeCompetitions) {
        this.activeCompetitions = activeCompetitions;
    }
}
