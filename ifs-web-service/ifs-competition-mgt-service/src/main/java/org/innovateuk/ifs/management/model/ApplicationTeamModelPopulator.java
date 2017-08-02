package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.management.viewmodel.ApplicationTeamViewModel;
import org.springframework.stereotype.Component;

/**
 * Populator for {@link ApplicationTeamViewModel}
 */
@Component
public class ApplicationTeamModelPopulator {

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
