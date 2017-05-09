package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.management.viewmodel.ReinstateIneligibleApplicationViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Management Reinstate Ineligible Application view.
 */
@Component
public class ReinstateIneligibleApplicationModelPopulator {

    public ReinstateIneligibleApplicationViewModel populateModel(final ApplicationResource applicationResource) {
        return new ReinstateIneligibleApplicationViewModel(applicationResource.getCompetition(),
                applicationResource.getId(), applicationResource.getName());
    }

}
