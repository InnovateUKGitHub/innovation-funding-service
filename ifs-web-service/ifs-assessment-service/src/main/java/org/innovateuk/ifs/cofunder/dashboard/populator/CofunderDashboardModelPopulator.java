package org.innovateuk.ifs.cofunder.dashboard.populator;

import org.innovateuk.ifs.cofunder.dashboard.viewmodel.CofunderDashboardViewModel;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.cofunder.service.CofunderDashboardRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Cofunder Dashboard view.
 */
@Component
public class CofunderDashboardModelPopulator {

    @Autowired
    private CofunderDashboardRestService cofunderDashboardRestService;

    // build the model for the comps
    public CofunderDashboardViewModel populateModel(UserResource user) {

        CofunderDashboardCompetitionResource cofunderDashboardCompetitionResource = cofunderDashboardRestService.getCofunderCompetitionDashboard(user.getId()).getSuccess();

        return new CofunderDashboardViewModel(
                cofunderDashboardCompetitionResource.getCofunderDashboardCompetitionUpcomingResource(),
                cofunderDashboardCompetitionResource.getCofunderDashboardCompetitionAwaitingResource());
    }
}
