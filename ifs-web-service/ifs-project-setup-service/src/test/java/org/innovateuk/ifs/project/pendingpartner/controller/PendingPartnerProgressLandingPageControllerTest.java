package org.innovateuk.ifs.project.pendingpartner.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.pendingpartner.populator.PendingPartnerProgressLandingPageViewModelPopulator;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.PendingPartnerProgressLandingPageViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PendingPartnerProgressLandingPageControllerTest extends BaseControllerMockMVCTest<PendingPartnerProgressLandingPageController> {

    @Override
    protected PendingPartnerProgressLandingPageController supplyControllerUnderTest() {
        return new PendingPartnerProgressLandingPageController();
    }

    @Mock
    private PendingPartnerProgressLandingPageViewModelPopulator populator;

    @Test
    public void progressLandingPage() throws Exception {
        long projectId = 1L;
        long organisationId = 2L;
        PendingPartnerProgressLandingPageViewModel viewModel = mock(PendingPartnerProgressLandingPageViewModel.class);
        when(populator.populate(projectId, organisationId)).thenReturn(viewModel);

        mockMvc.perform(get("/project/{projectId}/organisation/{organisationId}/pending-partner-progress", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/pending-partner-progress/landing-page"))
                .andExpect(model().attribute("model", viewModel));
    }
}
