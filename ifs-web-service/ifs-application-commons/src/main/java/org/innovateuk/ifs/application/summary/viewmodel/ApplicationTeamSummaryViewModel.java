package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationTeamResource;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamSummaryViewModel implements NewQuestionSummaryViewModel {
    private final ApplicationTeamResource team;

    public ApplicationTeamSummaryViewModel(ApplicationTeamResource team) {
        this.team = team;
    }

    public ApplicationTeamResource getTeam() {
        return team;
    }

    @Override
    public String getName() {
        return "Application team";
    }

    @Override
    public String getFragment() {
        return "application-team";
    }
}
