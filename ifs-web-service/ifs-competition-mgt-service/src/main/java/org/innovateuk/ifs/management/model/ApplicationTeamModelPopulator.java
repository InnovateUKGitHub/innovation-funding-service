package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.management.viewmodel.ApplicationTeamViewModel;

/**
 * Populator for {@Link ApplicationTeamViewModel}
 */
public class ApplicationTeamModelPopulator {
    public ApplicationTeamViewModel populateModel(ApplicationResource application, ApplicationTeamResource team) {
        ApplicationTeamViewModel applicationTeam = new ApplicationTeamViewModel();
        applicationTeam.setApplicationName(application.getName());
        applicationTeam.setTeam(team);
        return applicationTeam;
    }
}
