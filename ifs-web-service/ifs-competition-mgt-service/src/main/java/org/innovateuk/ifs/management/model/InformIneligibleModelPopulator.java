package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.management.viewmodel.InformIneligibleViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populator for {@link InformIneligibleViewModel}
 */
@Component
public class InformIneligibleModelPopulator {

    @Autowired
    private ApplicationService applicationService;

    public InformIneligibleViewModel populateModel(ApplicationResource applicationResource) {

        return new InformIneligibleViewModel(
                applicationResource.getCompetition(),
                applicationResource.getId(),
                applicationResource.getCompetitionName(),
                applicationResource.getName(),
                "Temp name");
    }
}
