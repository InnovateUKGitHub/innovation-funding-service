package org.innovateuk.ifs.cofunder.dashboard.viewmodel;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionPendingResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionPreviousResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionUpcomingResource;

import java.util.List;

/**
 * Holder of model attributes for the Cofunder Dashboard.
 */
public class CofunderDashboardViewModel {

    private List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions;
    private List<CofunderDashboardCompetitionPendingResource> activeCompetitions;
    private List<CofunderDashboardCompetitionPreviousResource> previousCompetitions;

    public CofunderDashboardViewModel(List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions,
                                      List<CofunderDashboardCompetitionPendingResource> activeCompetitions,
                                      List<CofunderDashboardCompetitionPreviousResource> previousCompetitions) {
        this.upcomingCompetitions = upcomingCompetitions;
        this.activeCompetitions = activeCompetitions;
        this.previousCompetitions = previousCompetitions;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public List<CofunderDashboardCompetitionPendingResource> getActiveCompetitions() {
        return activeCompetitions;
    }

    public List<CofunderDashboardCompetitionPreviousResource> getPreviousCompetitions() {
        return previousCompetitions;
    }
}
