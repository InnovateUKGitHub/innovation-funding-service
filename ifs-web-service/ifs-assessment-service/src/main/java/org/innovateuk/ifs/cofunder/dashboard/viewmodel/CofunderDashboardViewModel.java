package org.innovateuk.ifs.supporter.dashboard.viewmodel;

import org.innovateuk.ifs.supporter.resource.SupporterDashboardCompetitionResource;

import java.util.List;

/**
 * Holder of model attributes for the Supporter Dashboard.
 */
public class SupporterDashboardViewModel {

    private List<SupporterDashboardCompetitionResource> activeCompetitions;
    private List<SupporterDashboardCompetitionResource> previousCompetitions;

    public SupporterDashboardViewModel(List<SupporterDashboardCompetitionResource> activeCompetitions,
                                      List<SupporterDashboardCompetitionResource> previousCompetitions) {
        this.activeCompetitions = activeCompetitions;
        this.previousCompetitions = previousCompetitions;
    }

    public List<SupporterDashboardCompetitionResource> getActiveCompetitions() {
        return activeCompetitions;
    }

    public List<SupporterDashboardCompetitionResource> getPreviousCompetitions() {
        return previousCompetitions;
    }
}
