package org.innovateuk.ifs.management.application.view.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.management.application.view.viewmodel.ApplicationTeamViewModel;
import org.springframework.stereotype.Component;

/**
 * Populator for {@link ApplicationTeamViewModel}
 */
@Component
public class ApplicationTeamModelManagementPopulator {

    public ApplicationTeamViewModel populateModel(ApplicationResource application, ApplicationTeamResource team, String params) {
        ApplicationTeamViewModel applicationTeam = new ApplicationTeamViewModel();
        applicationTeam.setApplicationName(application.getName());
        applicationTeam.setTeam(team);
        applicationTeam.setApplicationId(application.getId());
        applicationTeam.setCompetitionId(application.getCompetition());
        applicationTeam.setQueryParams(params);
        return applicationTeam;
    }
}
