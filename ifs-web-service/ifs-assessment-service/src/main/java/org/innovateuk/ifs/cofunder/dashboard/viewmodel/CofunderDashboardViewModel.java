package org.innovateuk.ifs.cofunder.dashboard.viewmodel;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;

import java.util.List;

/**
 * Holder of model attributes for the Cofunder Dashboard.
 */
public class CofunderDashboardViewModel {

    private List<CofunderDashboardCompetitionResource> upcomingCompetitions;
    private List<CofunderDashboardCompetitionResource> activeCompetitions;
    private List<CofunderDashboardCompetitionResource> previousCompetitions;

    public CofunderDashboardViewModel(List<CofunderDashboardCompetitionResource> upcomingCompetitions,
                                      List<CofunderDashboardCompetitionResource> activeCompetitions,
                                      List<CofunderDashboardCompetitionResource> previousCompetitions) {
        this.upcomingCompetitions = upcomingCompetitions;
        this.activeCompetitions = activeCompetitions;
        this.previousCompetitions = previousCompetitions;
    }

    public List<CofunderDashboardCompetitionResource> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public List<CofunderDashboardCompetitionResource> getActiveCompetitions() {
        return activeCompetitions;
    }

    public List<CofunderDashboardCompetitionResource> getPreviousCompetitions() {
        return previousCompetitions;
    }
}
