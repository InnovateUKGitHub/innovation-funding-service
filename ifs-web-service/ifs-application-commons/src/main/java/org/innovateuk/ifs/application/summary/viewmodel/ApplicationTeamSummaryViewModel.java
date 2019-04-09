package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationTeamResource;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamSummaryViewModel {
    private final ApplicationTeamResource team;

    public ApplicationTeamSummaryViewModel(ApplicationTeamResource team) {
        this.team = team;
    }        ApplicationTeamResource teamResource = applicationSummaryRestService.getApplicationTeam(applicationId).getSuccess();


    public ApplicationTeamResource getTeam() {
        return team;
    }
}
