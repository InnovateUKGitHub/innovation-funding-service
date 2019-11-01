package org.innovateuk.ifs.dashboard.viewmodel;

import org.junit.Test;

import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardInSetupRowResource.DashboardInSetupRowResourceBuilder.aDashboardInSetupRowResource;
import static org.junit.Assert.assertEquals;

public class InSetupDashboardRowViewModelTest {

    @Test
    public void notPending() {
        InSetupDashboardRowViewModel viewModel = new InSetupDashboardRowViewModel(aDashboardInSetupRowResource()
                .withCompetitionTitle("Competition")
                .withApplicationId(1L)
                .withProjectId(2L)
                .withProjectTitle("Project")
                .withPendingPartner(false)
                .build());

        assertEquals("/project-setup/project/2", viewModel.getLinkUrl());
        assertEquals("Project", viewModel.getTitle());
    }

    @Test
    public void pending() {
        InSetupDashboardRowViewModel viewModel = new InSetupDashboardRowViewModel(aDashboardInSetupRowResource()
                .withCompetitionTitle("Competition")
                .withApplicationId(1L)
                .withProjectId(2L)
                .withProjectTitle("Project")
                .withPendingPartner(true)
                .withOrganisationId(3L)
                .build());

        assertEquals("/project-setup/project/2/organisation/3/pending-partner-progress", viewModel.getLinkUrl());
        assertEquals("Project", viewModel.getTitle());
    }
}
