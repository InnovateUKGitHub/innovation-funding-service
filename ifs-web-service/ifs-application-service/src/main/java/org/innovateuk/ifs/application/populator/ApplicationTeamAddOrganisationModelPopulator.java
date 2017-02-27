package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamAddOrganisationViewModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO
 */
public class ApplicationTeamAddOrganisationModelPopulator {

    @Autowired
    private ApplicationService applicationService;

    public ApplicationTeamAddOrganisationViewModel populateModel(long applicationId) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        return new ApplicationTeamAddOrganisationViewModel(applicationResource.getId(), applicationResource.getApplicationDisplayName());
    }

}