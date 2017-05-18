package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationTeamResource;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamViewModel {
    String applicationName;
    ApplicationTeamResource team;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public ApplicationTeamResource getTeam() {
        return team;
    }

    public void setTeam(ApplicationTeamResource team) {
        this.team = team;
    }
}
