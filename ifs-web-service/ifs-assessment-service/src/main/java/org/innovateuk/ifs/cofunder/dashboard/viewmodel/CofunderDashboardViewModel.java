package org.innovateuk.ifs.cofunder.dashboard.viewmodel;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionActiveResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionPreviousResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionUpcomingResource;

import java.util.List;

/**
 * Holder of model attributes for the Cofunder Dashboard.
 */
public class CofunderDashboardViewModel {

    private List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions;
    private List<CofunderDashboardCompetitionActiveResource> activeCompetitions;
    private List<CofunderDashboardCompetitionPreviousResource> previousCompetitions;

    public CofunderDashboardViewModel(List<CofunderDashboardCompetitionUpcomingResource> upcomingCompetitions,
                                      List<CofunderDashboardCompetitionActiveResource> activeCompetitions,
                                      List<CofunderDashboardCompetitionPreviousResource> previousCompetitions) {
        this.upcomingCompetitions = upcomingCompetitions;
        this.activeCompetitions = activeCompetitions;
        this.previousCompetitions = previousCompetitions;
    }

    public List<CofunderDashboardCompetitionUpcomingResource> getUpcomingCompetitions() {
        return upcomingCompetitions;
    }

    public List<CofunderDashboardCompetitionActiveResource> getActiveCompetitions() {
        return activeCompetitions;
    }

    public List<CofunderDashboardCompetitionPreviousResource> getPreviousCompetitions() {
        return previousCompetitions;
    }
}
