package org.innovateuk.ifs.cofunder.dashboard.populator;

import org.innovateuk.ifs.cofunder.dashboard.viewmodel.CofunderDashboardViewModel;
import org.innovateuk.ifs.cofunder.resource.AssessorDashboardState;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.cofunder.service.CofunderDashboardRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Build the model for the Cofunder Dashboard view.
 */
@Component
public class CofunderDashboardModelPopulator {

    @Autowired
    private CofunderDashboardRestService cofunderDashboardRestService;

    public CofunderDashboardViewModel populateModel(UserResource user) {

        Map<AssessorDashboardState, List<CofunderDashboardCompetitionResource>> cofunderDashboardCompetitionResources = cofunderDashboardRestService.getCofunderCompetitionDashboard(user.getId()).getSuccess();

        return new CofunderDashboardViewModel(
                cofunderDashboardCompetitionResources.get(AssessorDashboardState.UPCOMING),
                cofunderDashboardCompetitionResources.get(AssessorDashboardState.INFLIGHT),
                cofunderDashboardCompetitionResources.get(AssessorDashboardState.PREVIOUS));
    }
}