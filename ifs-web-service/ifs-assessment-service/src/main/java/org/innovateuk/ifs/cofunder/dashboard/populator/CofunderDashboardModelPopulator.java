package org.innovateuk.ifs.supporter.dashboard.populator;

import org.innovateuk.ifs.supporter.dashboard.viewmodel.SupporterDashboardViewModel;
import org.innovateuk.ifs.supporter.resource.AssessorDashboardState;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardCompetitionResource;
import org.innovateuk.ifs.supporter.service.SupporterDashboardRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Build the model for the Supporter Dashboard view.
 */
@Component
public class SupporterDashboardModelPopulator {

    @Autowired
    private SupporterDashboardRestService supporterDashboardRestService;

    public SupporterDashboardViewModel populateModel(UserResource user) {

        Map<AssessorDashboardState, List<SupporterDashboardCompetitionResource>> supporterDashboardCompetitionResources
                = supporterDashboardRestService.getSupporterCompetitionDashboard(user.getId()).getSuccess();

        return new SupporterDashboardViewModel(
                supporterDashboardCompetitionResources.get(AssessorDashboardState.INFLIGHT),
                supporterDashboardCompetitionResources.get(AssessorDashboardState.PREVIOUS));
    }
}