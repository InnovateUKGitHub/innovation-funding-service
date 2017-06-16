package org.innovateuk.ifs.application.team.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamAddOrganisationViewModel;
import org.springframework.stereotype.Component;

/**
 * Builds the model for the Add Organisation view.
 */
@Component
public class ApplicationTeamAddOrganisationModelPopulator {

    public ApplicationTeamAddOrganisationViewModel populateModel(ApplicationResource applicationResource) {
        return new ApplicationTeamAddOrganisationViewModel(applicationResource.getId(), applicationResource.getName());
    }

}